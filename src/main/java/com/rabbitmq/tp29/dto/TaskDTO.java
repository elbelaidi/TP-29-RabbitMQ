package com.rabbitmq.tp29.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for task messages with priority support.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskId;
    private String taskName;
    private String description;
    private String status;
    private int priority; // 1-10, higher = more priority
    private LocalDateTime createdAt;
    private LocalDateTime scheduledAt;
    private int retryCount;

    public static TaskDTO create(String taskName, String description, int priority) {
        return TaskDTO.builder()
                .taskId(java.util.UUID.randomUUID().toString())
                .taskName(taskName)
                .description(description)
                .status("PENDING")
                .priority(priority)
                .createdAt(LocalDateTime.now())
                .scheduledAt(LocalDateTime.now())
                .retryCount(0)
                .build();
    }

    public void incrementRetry() {
        this.retryCount++;
    }

    public void markCompleted() {
        this.status = "COMPLETED";
    }

    public void markFailed() {
        this.status = "FAILED";
    }
}

