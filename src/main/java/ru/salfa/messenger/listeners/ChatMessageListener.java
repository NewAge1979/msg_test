package ru.salfa.messenger.listeners;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
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
import ru.salfa.messenger.message.toUser.ExceptionPayload;
import ru.salfa.messenger.service.ChatService;
import ru.salfa.messenger.utils.validations.ValidationUtil;

import java.nio.charset.StandardCharsets;

import static ru.salfa.messenger.utils.SimpleObjectMapper.getObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageListener implements ChannelAwareMessageListener {

    private final ChatService chatService;
    private final WebSocketListenersHolder listeners;

    @Override
    @RabbitListener(queues = RabbitConfig.QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void onMessage(Message message, Channel channel) throws Exception {
        byte[] messageBody = message.getBody();
        String userPhoneNumber = message.getMessageProperties().getHeader("userPhoneNumber");
        MessageOutUser msg;
        try {
            msg = getObjectMapper().readValue(messageBody, MessageOutUser.class);
            ValidationUtil.validate(msg);
        } catch (Exception e) {
            log.error(e.getMessage());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

            var exceptionPayload = new ExceptionPayload();
            exceptionPayload.setRequest(new String(messageBody, StandardCharsets.UTF_8));
            if (e instanceof UnrecognizedPropertyException) {
                exceptionPayload.setException("An error occurred during message processing. Check messages field.");
            } else {
                exceptionPayload.setException(e.getMessage());
            }

            chatService.sendMessage(listeners.getListeners().get(userPhoneNumber), exceptionPayload);
            return;
        }

        log.info("Received a message from RabbitMQ: {}", msg);
        try {

            msg.handler(chatService, listeners.getListeners(),
                    userPhoneNumber);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Error processing message", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
        }
    }
}
