package ru.salfa.messenger.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.*;
import ru.salfa.messenger.message.MessageToUser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ChatService {

    @Transactional(readOnly = true)
    List<ChatsDto> getListChatDtoByUserPhone(String phone, Map<String, WebSocketSession> listeners);

    @Transactional(readOnly = true)
    List<MessageDto> searchMessage(Long chatId, String query, String userPhone);

    @Transactional(readOnly = true)
    List<MessageDto> getMessageByChat(Long chatId, String userPhone);

    boolean clearChat(Long chatId, String userPhone);

    MessageDto createAndSaveMsg(Long chatId, String senderPhone, String message, List<Document> attachments) throws JsonProcessingException;

    boolean deleteMessage(Long messageId, String userPhone, boolean isAll);

    boolean sendMessage(WebSocketSession session, MessageToUser message) throws IOException;

    ChatIsCreated getOrCreateChat(Long participantId, String userPhone);

    @Transactional
    List<UserDto> getListUserDtoByNickname(String nickname);

    @Transactional
    boolean blockedContact(Long userId, String phone);

    List<UserDto> getListUserDtoByPhone(String phone);

    void disconnectUser(String phoneNumber);

    void ackMessage(Long messageID);
}
