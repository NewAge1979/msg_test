package ru.salfa.messenger.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.AttachmentsDto;
import ru.salfa.messenger.dto.model.ChatIsCreated;
import ru.salfa.messenger.dto.model.ChatsDto;
import ru.salfa.messenger.dto.model.MessageDto;
import ru.salfa.messenger.message.MessageToUser;

import java.util.List;

public interface ChatService {
    @Transactional(readOnly = true)
    List<ChatsDto> getListChatDtoByUserPhone(String phone);

    @Transactional(readOnly = true)
    List<MessageDto> searchMessage(Long chatId, String query, String userPhone);

    void clearChat(Long chatId, String userPhone);

    MessageDto createAndSaveMsg(Long chatId, String senderPhone, String message, List<AttachmentsDto> attachments) throws JsonProcessingException;

    void deleteMessage(Long messageId, String userPhone, boolean isAll);

    void sendMessage(WebSocketSession session, MessageToUser message);

    ChatIsCreated getOrCreateChat(Long participantId, String userPhone);
}
