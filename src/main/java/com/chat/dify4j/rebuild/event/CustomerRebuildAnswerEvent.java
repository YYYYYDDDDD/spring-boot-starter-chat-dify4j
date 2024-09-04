package com.chat.dify4j.rebuild.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CustomerRebuildAnswerEvent extends ApplicationEvent {

    public ThreadLocal<StringBuilder> body;

    public CustomerRebuildAnswerEvent(Object source, ThreadLocal<StringBuilder> body) {
        super(source);
        this.body = body;
    }
}
