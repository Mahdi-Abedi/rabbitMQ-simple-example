package com.example.producer.controller;

import com.example.producer.entity.CustomData;
import com.example.producer.services.CustomDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/custom-data")
@RequiredArgsConstructor
public class CustomDataController {

    private final CustomDataService customDataService;

    @PostMapping
    public ResponseEntity<CustomData> create(@RequestBody CustomData customData) {
        var data = customDataService.createData(customData);
        return new ResponseEntity<>(data, HttpStatus.CREATED);
    }

    @GetMapping("/message")
    public ResponseEntity<String> sendMessage(@RequestBody String message) {
        var data = customDataService.sendMessageToRabbitmq(message);
        return ResponseEntity.ok("message send to RabbitMQ ...");
    }
}
