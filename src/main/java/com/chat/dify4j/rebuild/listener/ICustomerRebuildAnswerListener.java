package com.chat.dify4j.rebuild.listener;

import com.chat.dify4j.rebuild.event.CustomerRebuildAnswerEvent;
import org.springframework.context.event.EventListener;

/**
 * 在发送给前端应用之前, 用户可以修改输出, 重新构建或者修改
 */
public interface ICustomerRebuildAnswerListener {

    @EventListener
    void rebuildAnswer(CustomerRebuildAnswerEvent event);
}
