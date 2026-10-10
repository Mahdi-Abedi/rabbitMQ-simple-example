package com.example.producer.entity;

import com.example.producer.enums.CustomDataStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomData {

    private Long id;

    private Long anotherId;
    private String stringData;

    private List<CustomDataItem> items;

    private CustomDataStatus status;

    private LocalDateTime createdAt;
}
