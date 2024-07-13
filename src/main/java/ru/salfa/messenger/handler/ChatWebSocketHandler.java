package ru.salfa.messenger.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.ChatListPayload;
import ru.salfa.messenger.message.toUser.ExceptionPayload;
import ru.salfa.messenger.service.ChatService;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static ru.salfa.messenger.utils.SimpleObjectMapper.getObjectMapper;


@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final ChatService chatService;
    private final Map<String, WebSocketSession> listeners = new ConcurrentHashMap<>();

    @Override
    @SneakyThrows
    public void afterConnectionEstablished(WebSocketSession session) {
        var chatListPayload = new ChatListPayload();
        var phone = (Objects.requireNonNull(session.getPrincipal())).getName();
        chatListPayload.setChats(chatService.getListChatDtoByUserPhone(phone));
        chatService.sendMessage(session, chatListPayload);
        listeners.put(phone, session);

        log.info(String.format("New session connected! Connected listeners with phone %s : %s",
                phone, listeners.size()));
    }

    @Override
    @SneakyThrows
    public void handleTextMessage(@NonNull WebSocketSession session, @NotNull TextMessage message) {
        try {
            var msg = getObjectMapper().readValue(message.getPayload(), MessageOutUser.class);
            msg.handler(chatService, listeners,
                    (Objects.requireNonNull(session.getPrincipal())).getName());
        } catch (Exception e) {
            var msg = new ExceptionPayload();
            msg.setRequest(message.getPayload());
            msg.setException(e.getMessage());
            chatService.sendMessage(session, msg);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        listeners.remove((Objects.requireNonNull(session.getPrincipal())).getName());
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
