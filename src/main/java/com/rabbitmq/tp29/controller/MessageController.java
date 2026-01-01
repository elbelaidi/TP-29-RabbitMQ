package com.rabbitmq.tp29.controller;

import com.rabbitmq.tp29.dto.MessageDTO;
import com.rabbitmq.tp29.dto.TaskDTO;
import com.rabbitmq.tp29.service.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for sending messages to RabbitMQ.
 * Provides endpoints for various messaging patterns.
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessagePublisher messagePublisher;

    // ==================== Hello World Endpoints ====================

    /**
     * Send a simple hello message.
     * POST /api/messages/hello
     */
    @PostMapping("/hello")
    public ResponseEntity<Map<String, String>> sendHelloMessage(@RequestBody Map<String, String> request) {
        String content = request.getOrDefault("content", "Hello World!");
        messagePublisher.sendHelloMessage(content);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Hello message sent to queue");
        response.put("content", content);
        return ResponseEntity.ok(response);
    }

    // ==================== Direct Exchange Endpoints ====================

    /**
     * Send a notification message via direct exchange.
     * POST /api/messages/notification
     */
    @PostMapping("/notification")
    public ResponseEntity<Map<String, String>> sendNotification(@RequestBody Map<String, String> request) {
        String content = request.getOrDefault("content", "New notification");
        messagePublisher.sendNotification(content);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Notification sent via direct exchange");
        response.put("content", content);
        return ResponseEntity.ok(response);
    }

    // ==================== Topic Exchange Endpoints ====================

    /**
     * Send an info message via topic exchange.
     * POST /api/messages/info
     */
    @PostMapping("/info")
    public ResponseEntity<Map<String, String>> sendInfoMessage(@RequestBody Map<String, String> request) {
        String content = request.getOrDefault("content", "Info message");
        messagePublisher.sendInfoMessage(content);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Info message sent via topic exchange");
        response.put("content", content);
        return ResponseEntity.ok(response);
    }

    /**
     * Send a warning message via topic exchange.
     * POST /api/messages/warning
     */
    @PostMapping("/warning")
    public ResponseEntity<Map<String, String>> sendWarningMessage(@RequestBody Map<String, String> request) {
        String content = request.getOrDefault("content", "Warning message");
        messagePublisher.sendWarningMessage(content);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Warning message sent via topic exchange");
        response.put("content", content);
        return ResponseEntity.ok(response);
    }

    /**
     * Send an error message via topic exchange.
     * POST /api/messages/error
     */
    @PostMapping("/error")
    public ResponseEntity<Map<String, String>> sendErrorMessage(@RequestBody Map<String, String> request) {
        String content = request.getOrDefault("content", "Error message");
        messagePublisher.sendErrorMessage(content);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Error message sent via topic exchange");
        response.put("content", content);
        return ResponseEntity.ok(response);
    }

    // ==================== Work Queue Endpoints ====================

    /**
     * Send a work task to the work queue.
     * POST /api/messages/task
     */
    @PostMapping("/task")
    public ResponseEntity<Map<String, Object>> sendTask(@RequestBody Map<String, Object> request) {
        String taskName = (String) request.getOrDefault("taskName", "Default Task");
        String description = (String) request.getOrDefault("description", "Task description");
        int priority = ((Number) request.getOrDefault("priority", 5)).intValue();
        
        messagePublisher.sendWorkMessage(taskName, description, priority);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Task sent to work queue");
        response.put("taskName", taskName);
        response.put("priority", priority);
        return ResponseEntity.ok(response);
    }

    /**
     * Send a task with dead letter support.
     * POST /api/messages/task-with-dlx
     */
    @PostMapping("/task-with-dlx")
    public ResponseEntity<Map<String, Object>> sendTaskWithDLX(@RequestBody Map<String, Object> request) {
        String taskName = (String) request.getOrDefault("taskName", "DLX Task");
        String description = (String) request.getOrDefault("description", "Task with dead letter support");
        int priority = ((Number) request.getOrDefault("priority", 5)).intValue();
        
        TaskDTO task = TaskDTO.create(taskName, description, priority);
        messagePublisher.sendTask(task);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Task with DLX sent to task queue");
        response.put("taskId", task.getTaskId());
        response.put("taskName", taskName);
        return ResponseEntity.ok(response);
    }

    // ==================== Fanout Exchange Endpoints ====================

    /**
     * Send a broadcast message to all bound queues.
     * POST /api/messages/broadcast
     */
    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, String>> sendBroadcast(@RequestBody Map<String, String> request) {
        String content = request.getOrDefault("content", "Broadcast message");
        messagePublisher.sendBroadcast(content);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Broadcast sent to all queues via fanout exchange");
        response.put("content", content);
        return ResponseEntity.ok(response);
    }

    // ==================== Bulk Messages Endpoint ====================

    /**
     * Send multiple messages at once.
     * POST /api/messages/bulk
     */
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> sendBulkMessages(@RequestBody Map<String, Object> request) {
        String type = (String) request.getOrDefault("type", "hello");
        int count = ((Number) request.getOrDefault("count", 5)).intValue();
        String baseContent = (String) request.getOrDefault("content", "Message");
        
        for (int i = 0; i < count; i++) {
            String content = baseContent + " #" + (i + 1);
            switch (type.toLowerCase()) {
                case "info":
                    messagePublisher.sendInfoMessage(content);
                    break;
                case "warning":
                    messagePublisher.sendWarningMessage(content);
                    break;
                case "error":
                    messagePublisher.sendErrorMessage(content);
                    break;
                case "notification":
                    messagePublisher.sendNotification(content);
                    break;
                case "task":
                    messagePublisher.sendWorkMessage("Bulk Task " + i, "Bulk task description", 5);
                    break;
                default:
                    messagePublisher.sendHelloMessage(content);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Bulk messages sent");
        response.put("count", count);
        response.put("type", type);
        return ResponseEntity.ok(response);
    }
}

