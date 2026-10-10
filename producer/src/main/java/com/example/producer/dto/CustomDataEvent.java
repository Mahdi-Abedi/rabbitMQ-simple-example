package com.example.producer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomDataEvent {

    private Long id;

    private Long anotherId;
    private String stringData;
    private List<CustomDataItemDto> items;
    private String status;
    private LocalDateTime createdAt;
}
