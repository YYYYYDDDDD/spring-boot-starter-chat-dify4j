package com.chat.dify4j.enums;

/**
 * dify 请求url 枚举
 */
public enum ChatUrl {

    // 对话型应用
    CHAT,

    // 工作流应用
    FLOW;

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 根据类型名称获取枚举实例的方法
     * @param type 对话类型 和ChatUrl 枚举值一致 chat和flow
     * @return ChatUrl对象
     */
    public static ChatUrl fromString(String type) {
        for (ChatUrl rt : ChatUrl.values()) {
            if (rt.name().equalsIgnoreCase(type)) {
                return rt;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + type);
    }
}
