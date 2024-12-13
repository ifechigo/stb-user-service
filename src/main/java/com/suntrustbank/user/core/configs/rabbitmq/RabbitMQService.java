package com.suntrustbank.user.core.configs.rabbitmq;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RabbitMQService {

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // Setup exchanges, queues, and bindings dynamically
    public void setupDynamicMessaging(List<String> exchanges, List<String> queues, List<String> routingKeys) {
        rabbitMQConfig.setupRabbitMQ(connectionFactory, exchanges, queues, routingKeys);
    }

    // Publish a message to a specific exchange with a routing key
    public void publishMessage(String exchange, String routingKey, String message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        System.out.println("Message published to exchange: " + exchange + ", routingKey: " + routingKey + ", message: " + message);
    }
}
