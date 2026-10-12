package com.example.producer.functions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Supplier;

@Configuration
public class ProducerFunctions {
    @Bean
    public Supplier<String> sendMyMessage() {
        return () -> {
            String message = "message" + UUID.randomUUID();
            System.out.println("Sending message : " + message);
            return message;
        };
    }
}
