package com.example.consumer.functions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class ConsumerFunctions {

    @Bean
    public Consumer<String> processMyMessage() {
        return message -> System.out.println("Received: message=" + message);
    }
}
