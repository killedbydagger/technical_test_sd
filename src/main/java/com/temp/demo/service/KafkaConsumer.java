package com.temp.demo.service;

import com.temp.demo.dto.kafka.UserRegistrationDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "orders", groupId = "my-consumer-group-1")
    public void consume(UserRegistrationDTO dto) {

    }
}
