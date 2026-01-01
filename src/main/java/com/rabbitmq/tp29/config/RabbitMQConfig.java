package com.rabbitmq.tp29.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration class that sets up:
 * - Connection Factory
 * - Exchanges (Direct, Fanout, Topic, Headers)
 * - Queues
 * - Bindings
 * - Message Converters
 */
@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @Value("${spring.rabbitmq.port:5672}")
    private int port;

    @Value("${spring.rabbitmq.username:guest}")
    private String username;

    @Value("${spring.rabbitmq.password:guest}")
    private String password;

    // ==================== Queue Names ====================
    public static final String HELLO_QUEUE = "tp29.hello.queue";
    public static final String WORK_QUEUE = "tp29.work.queue";
    public static final String TASK_QUEUE = "tp29.task.queue";
    public static final String NOTIFICATION_QUEUE = "tp29.notification.queue";
    public static final String DEAD_LETTER_QUEUE = "tp29.dead.letter.queue";

    // ==================== Exchange Names ====================
    public static final String DIRECT_EXCHANGE = "tp29.direct.exchange";
    public static final String FANOUT_EXCHANGE = "tp29.fanout.exchange";
    public static final String TOPIC_EXCHANGE = "tp29.topic.exchange";
    public static final String HEADERS_EXCHANGE = "tp29.headers.exchange";
    public static final String DLX_EXCHANGE = "tp29.dlx.exchange";

    // ==================== Routing Keys ====================
    public static final String INFO_ROUTING_KEY = "tp29.info";
    public static final String WARNING_ROUTING_KEY = "tp29.warning";
    public static final String ERROR_ROUTING_KEY = "tp29.error";
    public static final String TASK_ROUTING_KEY = "tp29.task.#";
    public static final String NOTIFICATION_ROUTING_KEY = "tp29.notification";

    // ==================== Connection Factory ====================
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    // ==================== Message Converter ====================
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    // ==================== Exchanges ====================
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    // ==================== Queues ====================
    @Bean
    public Queue helloQueue() {
        return QueueBuilder.durable(HELLO_QUEUE).build();
    }

    @Bean
    public Queue workQueue() {
        return QueueBuilder.durable(WORK_QUEUE).build();
    }

    @Bean
    public Queue taskQueue() {
        return QueueBuilder.durable(TASK_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dead-letter")
                .build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    // ==================== Bindings ====================
    @Bean
    public Binding helloBinding(Queue helloQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(helloQueue).to(directExchange).with("hello");
    }

    @Bean
    public Binding workBinding(Queue workQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(workQueue).to(directExchange).with("work");
    }

    @Bean
    public Binding infoBinding(Queue workQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(workQueue).to(topicExchange).with(INFO_ROUTING_KEY);
    }

    @Bean
    public Binding warningBinding(Queue workQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(workQueue).to(topicExchange).with(WARNING_ROUTING_KEY);
    }

    @Bean
    public Binding errorBinding(Queue workQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(workQueue).to(topicExchange).with(ERROR_ROUTING_KEY);
    }

    @Bean
    public Binding taskBinding(Queue taskQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(taskQueue).to(topicExchange).with(TASK_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(notificationQueue).to(directExchange).with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public Binding fanoutBinding1(Queue helloQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(helloQueue).to(fanoutExchange);
    }

    @Bean
    public Binding fanoutBinding2(Queue workQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(workQueue).to(fanoutExchange);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("dead-letter");
    }
}

