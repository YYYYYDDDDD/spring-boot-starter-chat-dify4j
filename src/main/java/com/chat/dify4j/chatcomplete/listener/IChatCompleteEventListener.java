package com.chat.dify4j.chatcomplete.listener;

import com.chat.dify4j.chatcomplete.event.ChatCompleteEvent;
import org.springframework.context.event.EventListener;

public interface IChatCompleteEventListener {

    /**
     * 对话结束发送对话结果事件监听
     *          ChatCompleteEvent.fullAnswer 完整的回答内容(对话型应用chat: 完整的回答内容 工作流flow: 工作流的结果)
     *
     *          ChatCompleteEvent.fullResult 事件结束的任务信息
     *
     *          结束事件的所有信息实例:
     *                   chat:
     *                         {
     *                           "event": "message_end",
     *                           "message_id": "5e52ce04-874b-4d27-9045-b3bc80def685",
     *                           "conversation_id": "45701982-8118-4bc5-8e9b-64562b4555f2",
     *                           "task_id" : "fc574300-aac4-49dc-aed3-ef4a855fe579"
     *                         }
     *                   flow:
     *                              {
     *                              	"event": "workflow_finished",
     *                              	"workflow_run_id": "4ca4c090-936d-494d-8baf-6c5202a2c962",
     *                              	"task_id": "6e1a4299-2f6a-496f-a9c8-bac28c06c214",
     *                              	"data": {
     *                              		"id": "4ca4c090-936d-494d-8baf-6c5202a2c962",
     *                              		"workflow_id": "ca0f593c-1d3d-41a6-94e2-219c64156aa8",
     *                              		"sequence_number": 7,
     *                              		"status": "succeeded",
     *                              		"outputs": {
     *                              			"body": "{\"message\":\"Hello! How can I assist you today?\"}"
     *                                             },
     *                              		"error": null,
     *                              		"elapsed_time": 1.0491352650569752,
     *                              		"total_tokens": 18,
     *                              		"total_steps": 4,
     *                              		"created_by": {
     *                              			"id": "7e1e8d4f-fafb-4168-94eb-b9bcc79cb65c",
     *                              			"user": "abc-123"
     *                                     },
     *                              		"created_at": 1723628026,
     *                              		"finished_at": 1723628027,
     *                              		"files": []
     *                              		}
     *                              }
     */
    @EventListener
    void onChatCompleteEvent(ChatCompleteEvent event);
}
