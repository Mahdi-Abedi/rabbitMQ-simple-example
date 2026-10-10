package com.example.producer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.queue.name-custom-data}")
    private String queueCustomData;

    @Value("${rabbitmq.queue.name-message}")
    private String queueMessage;

    @Value("${rabbitmq.routing.key-message}")
    private String routingKeyMessage;

    @Value("${rabbitmq.routing.key-custom-data}")
    private String routingKeyCustomData;

    @Bean
    public Queue rabbitmqMessageQueue() {
        return QueueBuilder.durable(queueMessage).build();
    }

    @Bean
    public Queue rabbitmqCustomDataQueue() {
        return QueueBuilder.durable(queueCustomData).build();
    }

    @Bean
    public TopicExchange rabbitmqExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Binding rabbitmqMessageBinding() {
        return BindingBuilder
                .bind(rabbitmqMessageQueue())
                .to(rabbitmqExchange())
                .with(routingKeyMessage);
    }

    @Bean
    public Binding rabbitmqCustomDataBinding() {
        return BindingBuilder
                .bind(rabbitmqCustomDataQueue())
                .to(rabbitmqExchange())
                .with(routingKeyCustomData);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        template.setExchange(exchangeName);
        return template;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }
}
