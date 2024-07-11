package ru.salfa.messenger.listeners;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;
import ru.salfa.messenger.component.impl.WebSocketListenersHolder;
import ru.salfa.messenger.config.RabbitConfig;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.service.ChatService;
import ru.salfa.messenger.utils.validations.ValidationUtil;

import static ru.salfa.messenger.utils.SimpleObjectMapper.getObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageListener implements ChannelAwareMessageListener {

    private final ChatService chatService;
    private final WebSocketListenersHolder listeners;


    @Override
    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void onMessage(Message message, Channel channel) throws Exception {
        byte[] messageBody = message.getBody();
        var msg = getObjectMapper().readValue(messageBody, MessageOutUser.class);
        try {
            ValidationUtil.validate(msg);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return;
        }

        log.info("Received a message from RabbitMQ: {}", msg);
        msg.handler(chatService, listeners.getListeners(),
                message.getMessageProperties().getHeader("userPhoneNumber"));

    }
}
