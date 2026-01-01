package com.rabbitmq.tp29.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for simple text messages.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String content;
    private String type;
    private LocalDateTime timestamp;
    private String priority;

    public static MessageDTO create(String content, String type) {
        return MessageDTO.builder()
                .id(java.util.UUID.randomUUID().toString())
                .content(content)
                .type(type)
                .timestamp(LocalDateTime.now())
                .priority("NORMAL")
                .build();
    }
}

