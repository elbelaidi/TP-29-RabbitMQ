package com.rabbitmq.tp29;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration tests for the RabbitMQ TP29 application.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.rabbitmq.listener.simple.auto-startup=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration"
})
class RabbitMqTp29ApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring context loads successfully
    }
}

