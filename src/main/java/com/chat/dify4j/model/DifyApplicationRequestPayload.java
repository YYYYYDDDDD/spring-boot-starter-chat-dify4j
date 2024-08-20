package com.chat.dify4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public class DifyApplicationRequestPayload {


    /**
     * 阻塞模式 常量
     */
    private static final String DEFAULT_RESPONSE_MODE = "streaming";

    /**
     * 流式模式 常量
     */
    private static final String BLOCKING_RESPONSE_MODE = "blocking";

    /**
     * 对话类型 和ChatUrl 枚举值一致 chat和flow
     */
    private final Map<String, String> inputs;

    /**
     * chat类型的用户输入
     */
    private final String query;

    /**
     * 流式模式/阻塞模式 默认 streaming
     */
    @JsonProperty("response_mode")
    private final String responseMode;

    /**
     * 会话id
     */
    @JsonProperty("conversation_id")
    private final String conversationId;

    /**
     * userId
     */
    private final String user;

    public static Builder builder() {
        return new Builder();
    }

    private DifyApplicationRequestPayload(Builder builder) {
        this.inputs = builder.inputs;
        this.query = builder.query;
        this.responseMode = builder.responseMode;
        this.conversationId = builder.conversationId;
        this.user = builder.user;
    }


    /**
     * DifyApplicationRequestPayload构造类
     */
    public static class Builder {

        /**
         * 对话类型 和ChatUrl 枚举值一致 chat和flow
         */
        private Map<String, String> inputs = new HashMap<>();

        /**
         * chat类型的用户输入
         */
        private String query;

        /**
         * 流式模式/阻塞模式 默认 streaming
         */
        private String responseMode = DEFAULT_RESPONSE_MODE;

        /**
         * 会话id
         */
        private String conversationId;

        /**
         * userId
         */
        private String user;

        public Builder inputs(Map<String, String> inputs) {
            this.inputs = inputs;
            return this;
        }

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        /**
         * 切换为阻塞模式
         */
        public Builder responseModeBlocking() {
            this.responseMode = BLOCKING_RESPONSE_MODE;
            return this;
        }

        public Builder conversationId(String conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public DifyApplicationRequestPayload build() {
            return new DifyApplicationRequestPayload(this);
        }
    }

}
