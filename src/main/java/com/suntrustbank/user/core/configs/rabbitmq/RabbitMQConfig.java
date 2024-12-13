package com.suntrustbank.user.core.configs.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitMQConfig {

    public void setupRabbitMQ(ConnectionFactory connectionFactory,
                              List<String> exchangeNames,
                              List<String> queueNames,
                              List<String> routingKeys) {

        AmqpAdmin amqpAdmin = new RabbitAdmin(connectionFactory);

        // Validate input sizes
        if (exchangeNames.size() != queueNames.size() || queueNames.size() != routingKeys.size()) {
            throw new IllegalArgumentException("Exchange, Queue, and Routing Key lists must have the same size.");
        }

        // Loop through inputs to create exchanges, queues, and bindings
        for (int i = 0; i < exchangeNames.size(); i++) {
            String exchangeName = exchangeNames.get(i);
            String queueName = queueNames.get(i);
            String routingKey = routingKeys.get(i);

            // Create exchange
            Exchange exchange = ExchangeBuilder.topicExchange(exchangeName).durable(true).build();
            amqpAdmin.declareExchange(exchange);

            // Create queue
            Queue queue = new Queue(queueName, true);
            amqpAdmin.declareQueue(queue);

            // Bind queue to exchange with routing key
            Binding binding = BindingBuilder.bind(queue).to((TopicExchange) exchange).with(routingKey);
            amqpAdmin.declareBinding(binding);
        }
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setMissingQueuesFatal(false); // Ignore missing queues
        return container;
    }
}
