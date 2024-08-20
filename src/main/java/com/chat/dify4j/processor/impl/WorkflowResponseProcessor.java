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
 * Dify flow工作流方式返回结果处理器
 */
@Slf4j
public class WorkflowResponseProcessor extends ResponseProcessor {

    /**
     * 工作流方式响应的结束事件标识
     */
    private static final String END_EVENT = "workflow_finished";

    @Override
    public void processLine(String line, SseEmitter emitter) throws IOException {
        JsonNode lineJsonResult = objectMapper.readTree(line.replaceAll("data: ", ""));
        log.info("processLine >>> {}", lineJsonResult.toPrettyString());
        String event = lineJsonResult.path("event").asText();
        // 消息结束事件，收到此事件则代表流式返回结束
        if (StrUtil.equals(END_EVENT, event)) {
            send(emitter, lineJsonResult.path("data").path("outputs").path("body"));
            finishProcessing(emitter, lineJsonResult);
        }
    }

    @Override
    public String chatType() {
        return ChatUrl.FLOW.name();
    }
}
