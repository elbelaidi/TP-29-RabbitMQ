package com.rabbitmq.tp29.service;

import com.rabbitmq.tp29.config.RabbitMQConfig;
import com.rabbitmq.tp29.dto.MessageDTO;
import com.rabbitmq.tp29.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Service responsible for consuming messages from RabbitMQ queues.
 * Demonstrates different listener configurations and message handling patterns.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    // ==================== Hello Queue Consumer ====================

    /**
     * Consumes messages from the hello queue.
     * Simple hello world example.
     */
    @RabbitListener(queues = RabbitMQConfig.HELLO_QUEUE)
    public void consumeHelloMessage(MessageDTO message) {
        log.info("Received hello message: {}", message);
        try {
            // Simulate processing
            Thread.sleep(100);
            log.info("Processed hello message: {}", message.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error processing hello message", e);
        }
    }

    // ==================== Work Queue Consumer ====================

    /**
     * Consumes tasks from the work queue.
     * Demonstrates work queue pattern with fair dispatch.
     */
    @RabbitListener(queues = RabbitMQConfig.WORK_QUEUE)
    public void consumeWorkTask(TaskDTO task) {
        log.info("Received task: {} - {}", task.getTaskId(), task.getTaskName());
        try {
            // Simulate task processing
            log.info("Processing task: {} with priority {}", task.getTaskName(), task.getPriority());
            Thread.sleep(500);
            task.markCompleted();
            log.info("Task completed: {}", task.getTaskId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            task.markFailed();
            log.error("Error processing task", e);
        }
    }

    // ==================== Task Queue Consumer ====================

    /**
     * Consumes tasks from the task queue with dead letter support.
     * @throws Exception 
     */
    @RabbitListener(queues = RabbitMQConfig.TASK_QUEUE)
    public void consumeTask(TaskDTO task) {
        log.info("Received task from task queue: {} - {}", task.getTaskId(), task.getTaskName());
        try {
            // Simulate processing with potential failure
            log.info("Processing task: {}", task.getTaskName());
            
            if (task.getTaskName().toLowerCase().contains("fail")) {
                throw new RuntimeException("Simulated task failure");
            }
            
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Task processing interrupted", e);
            }
            task.markCompleted();
            log.info("Task completed successfully: {}", task.getTaskId());
        } catch (Exception e) {
            task.incrementRetry();
            log.error("Task processing failed: {}", task.getTaskId(), e);
            throw e; // Re-throw to trigger dead letter routing
        }
    }

    // ==================== Notification Queue Consumer ====================

    /**
     * Consumes notification messages.
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void consumeNotification(MessageDTO message) {
        log.info("Received notification: {} - {}", message.getId(), message.getContent());
        log.info("Notification type: {}", message.getType());
        log.info("Notification timestamp: {}", message.getTimestamp());
    }

    // ==================== Dead Letter Queue Consumer ====================

    /**
     * Consumes messages that failed processing and were routed to dead letter queue.
     */
    @RabbitListener(queues = RabbitMQConfig.DEAD_LETTER_QUEUE)
    public void consumeDeadLetter(MessageDTO message) {
        log.warn("Received dead letter message: {}", message);
        log.warn("Message type: {}", message.getType());
        log.warn("This message failed processing and was moved to dead letter queue");
        // In production, you might want to:
        // - Store in database for manual inspection
        // - Send alert to monitoring system
        // - Attempt recovery
    }

    // ==================== Topic Exchange Consumers ====================

    /**
     * Consumer for info routing key messages.
     */
    @RabbitListener(queues = RabbitMQConfig.WORK_QUEUE)
    public void consumeInfoMessage(MessageDTO message) {
        if ("INFO".equals(message.getType())) {
            log.info("Received INFO message: {}", message.getContent());
        }
    }

    /**
     * Consumer for warning routing key messages.
     */
    @RabbitListener(queues = RabbitMQConfig.WORK_QUEUE)
    public void consumeWarningMessage(MessageDTO message) {
        if ("WARNING".equals(message.getType())) {
            log.warn("Received WARNING message: {}", message.getContent());
        }
    }

    /**
     * Consumer for error routing key messages.
     */
    @RabbitListener(queues = RabbitMQConfig.WORK_QUEUE)
    public void consumeErrorMessage(MessageDTO message) {
        if ("ERROR".equals(message.getType())) {
            log.error("Received ERROR message: {}", message.getContent());
        }
    }
}

