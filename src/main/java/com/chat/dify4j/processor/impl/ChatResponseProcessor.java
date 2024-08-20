package com.chat.dify4j.processor.impl;

import cn.hutool.core.util.StrUtil;
import com.chat.dify4j.enums.ChatUrl;
import com.chat.dify4j.processor.ResponseProcessor;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * Dify 普通chat方式返回结果处理器
 */
@Slf4j
public class ChatResponseProcessor extends ResponseProcessor {

    /**
     * 普通chat方式响应的结束事件标识
     */
    private static final String END_EVENT = "message_end";

    @Override
    public void processLine(String line, SseEmitter emitter) throws IOException {
        JsonNode lineJsonResult = super.objectMapper.readTree(line.replaceAll("data: ", ""));
        log.info("processLine >>> {}", lineJsonResult.toPrettyString());
        String event = lineJsonResult.path("event").asText();
        super.send(emitter,lineJsonResult.path("answer"));
        // 消息结束事件，收到此事件则代表流式返回结束
        if (StrUtil.equals(END_EVENT, event)) {
            finishProcessing(emitter, lineJsonResult);
        }
    }

    @Override
    public String chatType() {
        return ChatUrl.CHAT.name();
    }
}
