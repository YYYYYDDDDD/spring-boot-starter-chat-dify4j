package com.chat.dify4j.processor;

import cn.hutool.core.util.StrUtil;
import com.chat.dify4j.chatcomplete.event.ChatCompleteEvent;
import com.chat.dify4j.model.DifyApplicationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Optional;

/**
 * Dify返回结果处理器抽象类
 */
public abstract class ResponseProcessor {


    /**
     * jackson ObjectMapper
     */
    public ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 返回完整回答的StringBuilder 使用ThreadLocal确保线程安全
     */
    public ThreadLocal<StringBuilder> threadLocalStringBuilder = ThreadLocal.withInitial(StringBuilder::new);


    /**
     * 对话结束发送对话结果事件发布者
     */
    @Autowired
    protected ApplicationEventPublisher eventPublisher;


    /**
     * 处理Dify返回结果流式中的某一行
     * @param line 流失相应的一次数据
     * @param emitter SseEmitter对象
     */
    abstract public void processLine(String line, SseEmitter emitter) throws IOException;

    /**
     * 像前端发送流
     * @param emitter SseEmitter对象
     * @param bodyNode 待发送的bodyNode对象
     */
    public void send(SseEmitter emitter, JsonNode bodyNode) throws IOException {
        if (!bodyNode.isMissingNode() && !bodyNode.isNull()) {
            StringBuilder fullAnswer = threadLocalStringBuilder.get();
            fullAnswer.append(bodyNode.asText());
            emitter.send(bodyNode.asText(), org.springframework.http.MediaType.TEXT_EVENT_STREAM);
        }
    }

    /**
     * 结束流输出, 并发送结果
     *
     * @param emitter SseEmitter对象
     */
    public void finishProcessing(SseEmitter emitter, JsonNode fullResult) {
        if (!ObjectUtils.isEmpty(emitter)) {
            emitter.complete();
        }
        DifyApplicationResponse difyApplicationResponse = DifyApplicationResponse.builder()
                .conversationId(Optional.ofNullable(fullResult)
                        .map(result -> result.path("conversation_id").asText())
                        .orElse(StrUtil.EMPTY))
                .taskId(Optional.ofNullable(fullResult)
                        .map(result -> result.path("task_id").asText())
                        .orElse(StrUtil.EMPTY))
                .type(this.chatType())
                .build();

        eventPublisher.publishEvent(new ChatCompleteEvent(this, threadLocalStringBuilder.get().toString(), difyApplicationResponse));
        // 移除threadLocal
        threadLocalStringBuilder.remove();
    }

    /**
     * 获取当前对话类型
     * @return 对象类型key
     */
    abstract public String chatType();
}
