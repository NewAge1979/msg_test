package ru.salfa.messenger.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.config.RabbitConfig;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.service.ChatService;
import ru.salfa.messenger.utils.validations.ValidationUtil;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

import static ru.salfa.messenger.utils.SimpleObjectMapper.getObjectMapper;

@Component
@RequiredArgsConstructor
public class ChatMessageListener {

    private final ChatService chatService;
    private final Map<String, WebSocketSession> listeners;
    private final Executor taskExecutor;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        taskExecutor.execute(() -> {
            try {
                var msg = getObjectMapper().readValue(message, MessageOutUser.class);
                ValidationUtil.validate(msg);
                msg.handler(chatService, listeners,
                        (Objects.requireNonNull(session.getPrincipal())).getName());
            } catch (Exception e) {
                // Обработка исключений и отправка сообщения об ошибке

            }
        });
    }
}
