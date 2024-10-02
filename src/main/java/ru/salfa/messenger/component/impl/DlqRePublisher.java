//package ru.salfa.messenger.component.impl;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import ru.salfa.messenger.config.RabbitConfig;
//
//@Component
//@RequiredArgsConstructor
//public class DlqRePublisher {
//
//    private final RabbitTemplate rabbitTemplate;
//
//    @Scheduled(fixedRate = 60000) // Период выполнения задачи (например, каждые 60 секунд)
//    public void rePublishMessages() {
//        boolean hasMoreMessages = true;
//        while (hasMoreMessages) {
//            Message message = rabbitTemplate.receive(RabbitConfig.DLQ_NAME);
//            if (message != null) {
//                rabbitTemplate.send(RabbitConfig.QUEUE_NAME, message);
//            } else {
//                hasMoreMessages = false;
//            }
//        }
//    }
//}
