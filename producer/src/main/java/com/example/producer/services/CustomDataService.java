package com.example.producer.services;

import com.example.producer.dto.CustomDataEvent;
import com.example.producer.dto.CustomDataItemDto;
import com.example.producer.entity.CustomData;
import com.example.producer.entity.CustomDataItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomDataService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key-message}")
    private String routingKeyMessage;

    @Value("${rabbitmq.routing.key-custom-data}")
    private String routingKeyCustomData;

    public String sendMessageToRabbitmq(String data) {

        publishStringCreatedEvent(data);

        return data;
    }

    public CustomData createData(CustomData data) {

        publishCustomDataCreatedEvent(data);

        return data;
    }

    private void publishCustomDataCreatedEvent(CustomData data) {
        var event = new CustomDataEvent(
                data.getId(),
                data.getAnotherId(),
                data.getStringData(),
                mapToCustomDataItemDtos(data.getItems()),
                data.getStatus().name(),
                data.getCreatedAt()
        );

        log.info("Custom Data id: {}", data.getId());

        rabbitTemplate.convertAndSend(exchangeName, routingKeyCustomData, event);
    }

    private void publishStringCreatedEvent(String data) {
        log.info("String data: {}", data);

        rabbitTemplate.convertAndSend(exchangeName, routingKeyMessage, Map.of("data", data));
    }

    private List<CustomDataItemDto> mapToCustomDataItemDtos(List<CustomDataItem> items) {
        return items.stream()
                .map(item -> new CustomDataItemDto(item.getId(), item.getItemName()))
                .toList();
    }

}
