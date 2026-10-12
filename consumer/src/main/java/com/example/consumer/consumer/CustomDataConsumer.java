package com.example.consumer.consumer;

import com.example.consumer.dto.CustomDataEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class CustomDataConsumer {

    @Bean
    public Consumer<CustomDataEvent> handleCustomDataEvent() {
        return event -> {
            log.info("ID: {}", event.getId());
            log.info("ANOTHER_ID: {}", event.getAnotherId());
            log.info("STRING_DATA: {}", event.getStringData());
            log.info("ITEMS: ");
            event.getItems().forEach(item -> log.info("\tID: {} , NAME: {}", item.getId(), item.getItemName()));
            log.info("STATUS: {}", event.getStatus());
            log.info("CREATED_AT: {}", event.getCreatedAt());
        };
    }
}
