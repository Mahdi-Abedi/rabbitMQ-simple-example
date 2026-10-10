package com.example.consumer.consumer;

import com.example.consumer.dto.CustomDataEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class MessageConsumer {

    @RabbitListener(queues = "${rabbitmq.queue.name-message}")
    public void handleMessageEvent(Map<String, String> map) {
        log.info("RECEIVED MESSAGE: {}" , map.get("data"));

    }
}
