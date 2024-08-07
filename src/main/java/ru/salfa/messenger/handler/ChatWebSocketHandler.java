package ru.salfa.messenger.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.salfa.messenger.component.impl.WebSocketListenersHolder;
import ru.salfa.messenger.config.RabbitConfig;
import ru.salfa.messenger.message.toUser.ChatListPayload;
import ru.salfa.messenger.message.toUser.ExceptionPayload;
import ru.salfa.messenger.service.ChatService;

import java.util.Objects;


@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final ChatService chatService;
    private final RabbitTemplate rabbitTemplate;
    private final WebSocketListenersHolder listeners;
    @Value("${websocket.textMessageSizeLimit}")
    private int textMessageSizeLimit;

    @Override
    @SneakyThrows
    public void afterConnectionEstablished(WebSocketSession session) {
        session.setTextMessageSizeLimit(textMessageSizeLimit);
        super.afterConnectionEstablished(session);
        var chatListPayload = new ChatListPayload();
        var phone = (Objects.requireNonNull(session.getPrincipal())).getName();
        chatListPayload.setChats(chatService.getListChatDtoByUserPhone(phone));
        chatService.sendMessage(session, chatListPayload);
        listeners.addListener(phone, session);

        log.info(String.format("New session connected! Connected listeners with phone %s : %s",
                phone, listeners.size()));
    }

    @Override
    @SneakyThrows
    public void handleTextMessage(@NonNull WebSocketSession session, @NotNull TextMessage message) {
        var phoneNumber = (Objects.requireNonNull(session.getPrincipal())).getName();
        MessageProperties messageProperties = MessagePropertiesBuilder.newInstance()
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setHeader("userPhoneNumber", phoneNumber)
                .build();
        Message rabbitMessage = MessageBuilder.withBody(message.getPayload().getBytes())
                .andProperties(messageProperties)
                .build();

        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, rabbitMessage);

    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        listeners.removeListener((Objects.requireNonNull(session.getPrincipal())).getName());
        log.info(String.format("Session disconnected. Total connected listeners: %s", listeners.size()));
    }

    @Override
    @SneakyThrows
    public void handleTransportError(@NonNull WebSocketSession session, Throwable exception) {
        var msg = new ExceptionPayload();
        log.error(exception.getMessage(), exception);
        msg.setException(exception.getMessage());
        chatService.sendMessage(session, msg);
    }
}
