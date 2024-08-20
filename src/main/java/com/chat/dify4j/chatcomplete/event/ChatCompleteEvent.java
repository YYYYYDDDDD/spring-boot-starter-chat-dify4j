package com.chat.dify4j.chatcomplete.event;

import com.chat.dify4j.model.DifyApplicationResponse;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ChatCompleteEvent extends ApplicationEvent {

    /**
     * 完整的回答内容(对话型应用chat: 完整的回答内容 工作流flow: 工作流的结果)
     */
    private final String fullAnswer;

    /**
     * 事件结束的任务信息
     */
    private final DifyApplicationResponse fullResult;

    public ChatCompleteEvent(Object source, String fullAnswer, DifyApplicationResponse fullResult) {
        super(source);
        this.fullAnswer = fullAnswer;
        this.fullResult = fullResult;
    }

}