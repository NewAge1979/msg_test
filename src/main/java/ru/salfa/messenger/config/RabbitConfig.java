package ru.salfa.messenger.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableRabbit
@EnableScheduling
public class RabbitConfig {

    public static final String QUEUE_NAME = "chatQueue";
    public static final String DLQ_NAME = "chatQueue.dlq";
    public static final String EXCHANGE_NAME = "chatExchange";
    public static final String DLX_NAME = "chatExchange.dlx";

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-max-length", 1000)
                .withArgument("x-max-length-bytes", 10485760)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-message-ttl", 60000) // TTL в миллисекундах (например, 60 секунд)
                .build();
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.durable(DLQ_NAME).build();
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange dlx() {
        return new DirectExchange(DLX_NAME);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(QUEUE_NAME);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlq()).to(dlx()).with(QUEUE_NAME);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3); // Количество параллельных слушателей
        factory.setMaxConcurrentConsumers(10); // Максимальное количество параллельных слушателей
        factory.setPrefetchCount(5); // Количество сообщений для предвыборки
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}

