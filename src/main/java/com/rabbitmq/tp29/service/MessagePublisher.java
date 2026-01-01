package com.rabbitmq.tp29.service;

import com.rabbitmq.tp29.config.RabbitMQConfig;
import com.rabbitmq.tp29.dto.MessageDTO;
import com.rabbitmq.tp29.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for publishing messages to RabbitMQ queues.
 * Demonstrates different exchange types: Direct, Fanout, Topic, and Headers.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    // ==================== Simple Hello World ====================

    /**
     * Sends a simple hello message to the hello queue.
     */
    public void sendHelloMessage(String content) {
        MessageDTO message = MessageDTO.create(content, "HELLO");
        log.info("Sending hello message: {}", message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.HELLO_QUEUE, message);
    }

    // ==================== Direct Exchange Examples ====================

    /**
     * Sends a notification message using direct exchange.
     */
    public void sendNotification(String content) {
        MessageDTO message = MessageDTO.create(content, "NOTIFICATION");
        log.info("Sending notification: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DIRECT_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                message
        );
    }

    // ==================== Topic Exchange Examples ====================

    /**
     * Sends an info message using topic exchange with info routing key.
     */
    public void sendInfoMessage(String content) {
        MessageDTO message = MessageDTO.create(content, "INFO");
        log.info("Sending info message: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOPIC_EXCHANGE,
                RabbitMQConfig.INFO_ROUTING_KEY,
                message
        );
    }

    /**
     * Sends a warning message using topic exchange with warning routing key.
     */
    public void sendWarningMessage(String content) {
        MessageDTO message = MessageDTO.create(content, "WARNING");
        log.info("Sending warning message: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOPIC_EXCHANGE,
                RabbitMQConfig.WARNING_ROUTING_KEY,
                message
        );
    }

    /**
     * Sends an error message using topic exchange with error routing key.
     */
    public void sendErrorMessage(String content) {
        MessageDTO message = MessageDTO.create(content, "ERROR");
        log.info("Sending error message: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOPIC_EXCHANGE,
                RabbitMQConfig.ERROR_ROUTING_KEY,
                message
        );
    }

    // ==================== Work Queue Example ====================

    /**
     * Sends a work message that will be processed by a worker.
     */
    public void sendWorkMessage(String taskName, String description, int priority) {
        TaskDTO task = TaskDTO.create(taskName, description, priority);
        log.info("Sending work task: {}", task);
        rabbitTemplate.convertAndSend(RabbitMQConfig.WORK_QUEUE, task);
    }

    // ==================== Task Queue with Dead Letter ====================

    /**
     * Sends a task to the task queue with dead letter support.
     * Failed tasks will be routed to the dead letter queue.
     */
    public void sendTask(TaskDTO task) {
        log.info("Sending task: {}", task);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOPIC_EXCHANGE,
                "tp29.task." + task.getTaskName().toLowerCase().replace(" ", "."),
                task
        );
    }

    // ==================== Fanout Exchange Example ====================

    /**
     * Sends a broadcast message using fanout exchange.
     * All bound queues will receive the message.
     */
    public void sendBroadcast(String content) {
        MessageDTO message = MessageDTO.create(content, "BROADCAST");
        log.info("Sending broadcast message: {}", message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", message);
    }
}

