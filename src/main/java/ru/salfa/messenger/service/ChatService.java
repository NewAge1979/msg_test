package ru.salfa.messenger.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.AttachmentsDto;
import ru.salfa.messenger.dto.model.ChatIsCreated;
import ru.salfa.messenger.dto.model.ChatsDto;
import ru.salfa.messenger.dto.model.MessageDto;
import ru.salfa.messenger.entity.Messages;
import ru.salfa.messenger.entity.User;
import ru.salfa.messenger.message.MessageToUser;

import java.util.List;

public interface ChatService {
    @Transactional(readOnly = true)
    List<ChatsDto> getListChatDtoByUser(User user) throws JsonProcessingException;
    @Transactional(readOnly = true)
    List<MessageDto> searchMessage(Long chatId, String query, Long userId);
    Messages saveMessage(Messages messages);
    void clearChat(Long chatId, Long userId);
    MessageDto createAndSaveMsg(Long chatId, Long senderId, String message, List<AttachmentsDto> attachments) throws JsonProcessingException;
    void deleteMessage(Long messageId, Long userId, boolean isAll);
    void sendMessage(WebSocketSession session, MessageToUser message);
    ChatIsCreated getChat(Long participantId, Long userId);
}
