package com.chat.dify4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class DifyApplicationResponse {

    /**
     * 会话id
     */
    @JsonProperty("conversation_id")
    private String conversationId;

    /**
     * 对话类型 和ChatUrl 枚举值一致 chat和flow
     */
    private String type;

    /**
     * 任务id
     */
    @JsonProperty("task_id")
    private String taskId;

}
