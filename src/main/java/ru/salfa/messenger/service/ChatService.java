package ru.salfa.messenger.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.*;
import ru.salfa.messenger.message.MessageToUser;

import java.io.IOException;
import java.util.List;

public interface ChatService {
    @Transactional(readOnly = true)
    List<ChatsDto> getListChatDtoByUserPhone(String phone);

    @Transactional(readOnly = true)
    List<MessageDto> searchMessage(Long chatId, String query, String userPhone);

    boolean clearChat(Long chatId, String userPhone);

    MessageDto createAndSaveMsg(Long chatId, String senderPhone, String message, List<Document> attachments) throws JsonProcessingException;

    boolean deleteMessage(Long messageId, String userPhone, boolean isAll);

    boolean sendMessage(WebSocketSession session, MessageToUser message) throws IOException;

    ChatIsCreated getOrCreateChat(Long participantId, String userPhone);

    @Transactional
    List<UserDto> getListUserDtoByNickname(String nickname);

    @Transactional
    boolean blockedContact(Long userId, String phone);
}
