# java对接dify应用的API

# 项目简介
[Dify](https://cloud.dify.ai/)是一款开源的大语言模型(LLM) 应用开发平台。它融合了后端即服务（Backend as Service）和 LLMOps 的理念，使开发者可以快速搭建生产级的生成式 AI 应用。

本项目旨在对接 Dify 大模型不同应用的 API，从而**对接自己业务系统**，实现与 Dify 应用的对话流处理，将对话结果流式返回给前端，并将对话结果分发给开发者自行处理。

由于是为对接本人所参与的业务系统，有些功能还不是很健壮和通用，后续有精力争取完善
目前是一个微服务的方式，有时间准备做一个`springboot starter`的方式

## 主要功能

- **对话流处理**: 根据不同的对话类型：【对话型应用】和【Workflow应用】，通过 Dify 的 API 处理用户输入，并返回相应的响应
- **多处理器支持**: 实现`ResponseProcessor`，处理不同的对话类型，方便日后系统拓展新的对话类型
- **基于 SSE (Server-Sent Events)**: 处理和推送来自 Dify 平台的对话响应，确保流式数据处理和实时更新
- **将每一轮对话结束的结果发送给开发者**: 单轮结束返回完整的回答内容以及对话源信息

## 使用说明

> [!IMPORTANT]
> 确保在请求时使用有效的 Dify 应用 `apiKey`

### 配置文件
```properties
# 对话型应用URL
dify.chat-url=https://api.dify.ai/v1/chat-messages
# Workflow应用URL
dify.flow-url=https://api.dify.ai/v1/workflows/run

# 开启代理 本地代理或正向代理
dify.proxy-enabled=false
# 代理地址
dify.proxy-host=127.0.0.1
# 代理端口
dify.proxy-port=7890
```




### 示例代码
- 方法调用

```java
@Autowired
private DifyApplicationChatHandler chatHandler;

  public void chat() throws JsonProcessingException {
        // 对话型应用:chat  Workflow应用:flow
        String type = "chat";

        // dify apikey
        String apiKey = "your-dify-api-key";

        // 对话型应用的用户输入
        String query = "用户输入的查询";

        // Dify App 定义的各变量值。 inputs 参数包含了多组键值对（Key/Value pairs），每组的键对应一个特定变量，每组的值则是该变量的具体值
        String inputs = "{\"key1\":\"value1\", \"key2\":\"key2\"}";
        
        // 会话ID
        String conversationId = "会话ID";
        
        // 用户标识
        String userId = "用户标识";

        SseEmitter emitter = chatHandler.handle(type, apiKey, query, inputs, conversationId, userId);
    }
```

- controller demo

```java
   @Autowired
   private DifyApplicationChatHandler chatHandler;

   @GetMapping("/chat")
    public ResponseEntity<SseEmitter> chat(@RequestParam("type") String type,
                                             @RequestParam("apiKey") String apiKey,
                                             @RequestParam(value = "query", required = false) String query,
                                             @RequestParam(value = "inputs", required = false) String inputs,
                                             @RequestParam(value = "conversationId", required = false) String conversationId,
                                             @RequestParam("userId") String userId) throws JsonProcessingException {

        SseEmitter emitter = difyApplicationChatHandler.handle(type, apiKey, query, inputs, conversationId, userId);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new org.springframework.http.MediaType("text", "event-stream", StandardCharsets.UTF_8));

        return new ResponseEntity<>(emitter, headers, HttpStatus.OK);
    }
```
### 接收对话结束的结果

实现`IChatCompleteEventListener`接口

```java
@Component
public class ChatCompleteEventListener implements IChatCompleteEventListener{

    public void onChatCompleteEvent(ChatCompleteEvent event) {
        // 完整的回答内容(对话型应用chat: 完整的回答内容 工作流flow: 工作流的结果)
        String fullAnswer = event.getFullAnswer();
        DifyApplicationResponse fullResult = event.getFullResult();
        System.out.println("Received chat completion fullAnswer: " + fullAnswer);
        System.out.println("Received chat completion fullResult: " + fullResult);
    }
}
```
其中，`DifyApplicationResponse`，由于我的业务不需要额外的字段，只接收了会话id和任务id，其他参数开发者可以自行填充，参考Dify官方的响应体

```java
@Data
@AllArgsConstructor
@Builder
@ToString
public class DifyApplicationResponse {

    /**
     * 会话id(只有对话型应用有)
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
```
