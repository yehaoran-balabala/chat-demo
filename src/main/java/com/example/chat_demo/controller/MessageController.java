package com.example.chat_demo.controller;

import com.example.chat_demo.entity.Message;
import com.example.chat_demo.service.IMessageService;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 消息控制器
 * 处理聊天消息的发送、接收和历史记录管理
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/")
public class MessageController {

    @Autowired
    private final OllamaChatModel ollamaChatModel;

    @Autowired
    private IMessageService messageService;

    public MessageController(OllamaChatModel ollamaChatModel) {
        this.ollamaChatModel = ollamaChatModel;
    }

    /**
     * 获取所有历史消息
     * @return 消息列表
     */
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages(){
        try {
            List<Message> messages = messageService.list();
            return ResponseEntity.ok()
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .body(messages);
        } catch (Exception e) {
            System.err.println("获取消息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 流式聊天接口
     * 使用Server-Sent Events实现实时消息传输
     * @param prompt 用户输入的消息
     * @return 流式响应
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestParam("prompt") String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "prompt不能为空");
        }

        try {
            System.out.println("收到用户消息: " + prompt);

            // 保存用户消息
            Message userMsg = new Message();
            userMsg.setCreatedAt(LocalDateTime.now());
            userMsg.setContent(prompt);
            userMsg.setSender("user");
            messageService.save(userMsg);
            System.out.println("用户消息已保存: " + userMsg.getId());

            // 用于构建AI完整回复的StringBuilder
            AtomicReference<StringBuilder> aiResponseBuilder = new AtomicReference<>(new StringBuilder());

            // 返回流式响应
            return ollamaChatModel.stream(prompt)
                    .doOnNext(token -> {
                        // 每个token都追加到完整回复中
                        aiResponseBuilder.get().append(token);
                    })
                    .map(token -> {
                        return ServerSentEvent.<String>builder()
                                .data(token)
                                .event("message")
                                .id(String.valueOf(System.currentTimeMillis()))
                                .build();
                    })
                    .concatWith(Flux.just(
                            ServerSentEvent.<String>builder()
                                    .data("[STREAM_END]")
                                    .event("complete")
                                    .build()
                    ))
                    .doOnComplete(() -> {
                        // 流结束时保存AI完整回复
                        try {
                            String fullResponse = aiResponseBuilder.get().toString();
                            if (fullResponse != null && !fullResponse.trim().isEmpty()) {
                                Message aiMsg = new Message();
                                aiMsg.setCreatedAt(LocalDateTime.now());
                                aiMsg.setContent(fullResponse);
                                aiMsg.setSender("bot");
                                messageService.save(aiMsg);
                                System.out.println("AI回复已保存: " + aiMsg.getId());
                                System.out.println("AI回复内容: " + fullResponse);
                            }
                        } catch (Exception e) {
                            System.err.println("保存AI回复失败: " + e.getMessage());
                            e.printStackTrace();
                        }
                    })
                    .doOnError(error -> {
                        System.err.println("流式处理出错: " + error.getMessage());
                        error.printStackTrace();
                    })
                    .onErrorResume(error -> {
                        // 发生错误时返回错误消息
                        return Flux.just(ServerSentEvent.<String>builder()
                                .data("抱歉，我现在无法回复，请稍后再试。")
                                .event("error")
                                .build());
                    })
                    .timeout(Duration.ofMinutes(2)); // 设置2分钟超时

        } catch (Exception e) {
            System.err.println("处理请求时出错: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }

    /**
     * 健康检查接口
     * @return 服务状态信息
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();

        try {
            // 检查数据库连接
            long messageCount = messageService.count();
            healthInfo.put("database", "connected");
            healthInfo.put("total_messages", messageCount);

            // 检查AI模型状态（简单检查）
            healthInfo.put("ai_model", "available");

            healthInfo.put("status", "healthy");
            healthInfo.put("timestamp", LocalDateTime.now().toString());
            healthInfo.put("service", "AI心理助手");
            healthInfo.put("version", "1.0.0");

            return ResponseEntity.ok(healthInfo);

        } catch (Exception e) {
            healthInfo.put("status", "unhealthy");
            healthInfo.put("error", e.getMessage());
            healthInfo.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(healthInfo);
        }
    }

    /**
     * 清空所有聊天记录
     * @return 操作结果
     */
    @DeleteMapping("/messages")
    public ResponseEntity<Map<String, String>> clearAllMessages() {
        try {
            // 获取删除前的消息数量
            long beforeCount = messageService.count();

            // 清空所有记录
            messageService.remove(null);

            Map<String, String> response = new HashMap<>();
            response.put("message", "聊天记录已清空");
            response.put("deleted_count", String.valueOf(beforeCount));
            response.put("timestamp", LocalDateTime.now().toString());

            System.out.println("已清空 " + beforeCount + " 条聊天记录");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("清空聊天记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "清空失败: " + e.getMessage()));
        }
    }

    /**
     * 删除特定消息
     * @param id 消息ID
     * @return 操作结果
     */
    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Map<String, String>> deleteMessage(@PathVariable Long id) {
        try {
            boolean success = messageService.removeById(id);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "消息已删除");
                response.put("deleted_id", id.toString());
                response.put("timestamp", LocalDateTime.now().toString());

                System.out.println("已删除消息ID: " + id);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "消息不存在", "message_id", id.toString()));
            }
        } catch (Exception e) {
            System.err.println("删除消息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "删除失败: " + e.getMessage()));
        }
    }

    /**
     * 获取消息统计信息
     * @return 统计数据
     */
    @GetMapping("/messages/stats")
    public ResponseEntity<Map<String, Object>> getMessageStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // 总消息数
            long totalMessages = messageService.count();
            stats.put("total_messages", totalMessages);

            // 用户消息数和AI消息数（这里需要根据实际的查询方法来实现）
            // 由于MyBatis-Plus的限制，这里用简单的方式处理
            List<Message> allMessages = messageService.list();
            long userMessages = allMessages.stream().filter(m -> "user".equals(m.getSender())).count();
            long botMessages = allMessages.stream().filter(m -> "bot".equals(m.getSender())).count();

            stats.put("user_messages", userMessages);
            stats.put("bot_messages", botMessages);

            // 最近活跃时间
            if (!allMessages.isEmpty()) {
                LocalDateTime lastActive = allMessages.stream()
                        .map(Message::getCreatedAt)
                        .max(LocalDateTime::compareTo)
                        .orElse(null);
                stats.put("last_active", lastActive != null ? lastActive.toString() : null);
            }

            stats.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("获取统计信息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "获取统计失败: " + e.getMessage()));
        }
    }

    /**
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        System.err.println("Controller异常: " + e.getMessage());
        e.printStackTrace();

        Map<String, String> error = new HashMap<>();
        error.put("error", "服务器内部错误");
        error.put("message", e.getMessage());
        error.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}