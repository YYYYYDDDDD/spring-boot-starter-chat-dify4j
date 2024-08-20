package com.chat.dify4j.config;

import com.chat.dify4j.handler.DifyApplicationChatHandler;
import com.chat.dify4j.processor.ResponseProcessor;
import com.chat.dify4j.processor.impl.ChatResponseProcessor;
import com.chat.dify4j.processor.impl.WorkflowResponseProcessor;
import com.chat.dify4j.properties.ChatUrlInitializer;
import com.chat.dify4j.properties.ChatUrlProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@EnableConfigurationProperties(value = ChatUrlProperties.class)
@Configuration
public class ChatDify4jAutoConfiguration {


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @ConditionalOnProperty(name = "dify.proxy-enabled", havingValue = "true")
    @Bean
    public Proxy proxy(ChatUrlProperties chatUrlProperties) {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(chatUrlProperties.getProxyHost(), chatUrlProperties.getProxyPort()));
    }

    @Bean
    public OkHttpClient okHttpClient(Optional<Proxy> proxy) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS);
        proxy.ifPresent(builder::proxy);
        return builder.build();
    }

    @Bean
    public DifyApplicationChatHandler difyApplicationChatHandler(Map<String, ResponseProcessor> responseProcessor,
                                                                 ObjectMapper objectMapper,
                                                                 OkHttpClient okHttpClient) {
        return new DifyApplicationChatHandler(responseProcessor, objectMapper, okHttpClient);
    }

    @Bean
    public ChatResponseProcessor chatProcessor() {
        return new ChatResponseProcessor();
    }

    @Bean
    public WorkflowResponseProcessor flowProcessor() {
        return new WorkflowResponseProcessor();
    }

    @Bean
    public ChatUrlInitializer chatUrlInitializer(ChatUrlProperties chatUrlProperties) {
        return new ChatUrlInitializer(chatUrlProperties);
    }
}
