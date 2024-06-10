package ru.salfa.messenger.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.ChatListPayload;
import ru.salfa.messenger.repository.UserRepository;
import ru.salfa.messenger.service.ChatService;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static ru.salfa.messenger.utils.SimpleObjectMapper.getObjectMapper;


@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final Map<Long, WebSocketSession> listeners = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Этот метод вызывается после установления соединения WebSocket

        var user = userRepository.findByPhone(Objects.requireNonNull(session.getPrincipal()).getName()).get();
        var chatListPayload = new ChatListPayload();

        try {
            chatListPayload.setChats(chatService.getListChatDtoByUser(user));
            chatService.sendMessage(session, chatListPayload);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        listeners.put(user.getId(), session);

        log.info(String.format("New session connected! Connected listeners: %s", listeners.size()));
    }

    @Override
    @Transactional
    public void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws IOException {
        // Обработка входящего сообщения
        var user = userRepository.findByPhone(Objects.requireNonNull(session.getPrincipal()).getName()).get();
        var msg = getObjectMapper().readValue(message.getPayload(), MessageOutUser.class);
        msg.handler(chatService, listeners, user.getId());
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        // Этот метод вызывается после закрытия соединения WebSocket
        var user = userRepository.findByPhone(Objects.requireNonNull(session.getPrincipal()).getName()).get();

        listeners.remove(user.getId());
        log.info(String.format("Session disconnected. Total connected listeners: %s", listeners.size()));
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, Throwable exception) {
        log.error(exception.getMessage());
    }
}
