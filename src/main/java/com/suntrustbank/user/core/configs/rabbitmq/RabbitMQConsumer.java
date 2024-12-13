package com.suntrustbank.user.core.configs.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {
//    @RabbitListener(queues = "queue1")
    public void listenToQueue1(String message) {
        System.out.println("Received message from queue1: " + message);
    }
}
