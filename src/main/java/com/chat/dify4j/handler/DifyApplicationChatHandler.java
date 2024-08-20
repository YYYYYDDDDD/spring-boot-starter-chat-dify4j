package com.chat.dify4j.handler;

import cn.hutool.core.util.StrUtil;
import com.chat.dify4j.enums.ChatUrl;
import com.chat.dify4j.model.DifyApplicationRequestPayload;
import com.chat.dify4j.processor.ResponseProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Dify应用对话处理器
 */
@Slf4j
@AllArgsConstructor
public class DifyApplicationChatHandler {

    /**
     * ResponseProcessor实现类Map key: bean名 value: bean实例
     */
    private Map<String, ResponseProcessor> responseProcessor;

    /**
     * jackson ObjectMapper
     */
    private final ObjectMapper objectMapper;

    /**
     * OkHttpClient
     */
    private final OkHttpClient client;


    /**
     * 对话流失处理
     * @param type 对话类型 和ChatUrl 枚举值一致 chat和flow
     * @param apiKey Dify应用apiKey
     * @param query chat类型的用户输入
     * @param inputs 对应Dify应用中的提示词变量, json字符串格式
     * @param conversationId 会话id
     * @param userId 用户id
     */
    public SseEmitter handle(String type, String apiKey, String query, String inputs, String conversationId, String userId) throws JsonProcessingException {
        SseEmitter emitter = new SseEmitter();
        RequestBody requestBody;

        // 请求体
        DifyApplicationRequestPayload payload = DifyApplicationRequestPayload
                .builder()
                .inputs(objectMapper.readValue(inputs, new TypeReference<Map<String, String>>() {}))
                .conversationId(conversationId)
                .query(query)
                .user(userId)
                .build();
        log.info("DifyApplicationRequestPayload >>> {}, type >>> {}, apiKey >>> {}", payload, type, apiKey);
        try {
            requestBody = RequestBody.create(
                    MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE),
                    objectMapper.writeValueAsString(payload)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Request request = new Request.Builder()
                .url(ChatUrl.fromString(type).getUrl())
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .addHeader(HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                emitter.completeWithError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    log.error("call fail : {}", new String(response.body().bytes()));
                    emitter.completeWithError(new IOException("Unexpected code " + response));
                    return;
                }

                ResponseProcessor responseProcessor = chatResponseProcessor(type);
                try (ResponseBody responseBody = response.body()) {
                    if (responseBody != null) {
                        BufferedSource source = responseBody.source();
                        while (!source.exhausted()) {
                            String line = source.readUtf8LineStrict();
                            if (StrUtil.isEmpty(line.trim())) {
                                continue;
                            }
                            responseProcessor.processLine(line, emitter);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    responseProcessor.finishProcessing(emitter, null);
                }
            }
        });
        return emitter;
    }

    private ResponseProcessor chatResponseProcessor(String type) {
        return responseProcessor.get(type + "Processor");
    }
}
