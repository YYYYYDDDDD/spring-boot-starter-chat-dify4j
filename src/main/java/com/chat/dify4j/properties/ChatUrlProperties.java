package com.chat.dify4j.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *  dify 请求url 配置类
 */
@ConfigurationProperties(prefix = "dify")
@Data
public class ChatUrlProperties {

    /**
     * 对话型应用URL
     */
    private String chatUrl;

    /**
     * Workflow应用URL
     */
    private String flowUrl;

    /**
     * 是否启用代理
     */
    private boolean proxyEnabled;

    /**
     * 代理地址
     */
    private String proxyHost;

    /**
     * 代理端口
     */
    private int proxyPort;

}
