package com.example.producer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomDataItem {

    private Long id;

    private String itemName;

    private CustomData customData;
}
