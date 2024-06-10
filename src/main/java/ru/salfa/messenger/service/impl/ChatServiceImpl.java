package ru.salfa.messenger.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.AttachmentsDto;
import ru.salfa.messenger.dto.model.ChatIsCreated;
import ru.salfa.messenger.dto.model.ChatsDto;
import ru.salfa.messenger.dto.model.MessageDto;
import ru.salfa.messenger.entity.Chat;
import ru.salfa.messenger.entity.Messages;
import ru.salfa.messenger.entity.User;
import ru.salfa.messenger.message.MessageToUser;
import ru.salfa.messenger.repository.AttachmentsRepository;
import ru.salfa.messenger.repository.ChatRepository;
import ru.salfa.messenger.repository.MessageRepository;
import ru.salfa.messenger.repository.UserRepository;
import ru.salfa.messenger.service.ChatService;
import ru.salfa.messenger.utils.mapper.AttachmentsMapper;
import ru.salfa.messenger.utils.mapper.ChatMapper;
import ru.salfa.messenger.utils.mapper.MessageMapper;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static ru.salfa.messenger.utils.SimpleObjectMapper.getObjectMapper;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final AttachmentsRepository attachmentsRepository;
    private final ObjectMapper jsonMapper = getObjectMapper();
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;
    private final AttachmentsMapper attachmentsMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ChatsDto> getListChatDtoByUser(User user) {
        return getChatsByParticipantId(user.getId()).stream()
                .map(chat -> mapChatToChatDto(chat, user))
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> searchMessage(Long chatId, String query, Long userId) {
        User user = getUserById(userId);
        return messageRepository.findMessagesByChatId(getChatById(chatId)).stream()
                .filter(msg -> !msg.isDelete() && !msg.getUserDeleteMessage().contains(user) &&
                        msg.getMessage().toLowerCase(Locale.ROOT).contains(query.toLowerCase()))
                .map(messageMapper::toMessageDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void clearChat(Long chatId, Long userId) {
        User user = getUserById(userId);
        messageRepository.findMessagesByChatId(getChatById(chatId)).forEach(msg -> msg.addUserDeleters(user));
    }

    @Override
    @Transactional
    public MessageDto createAndSaveMsg(Long chatId, Long senderId, String message, List<AttachmentsDto> attachments) {
        Chat chat = getChatById(chatId);
        User sender = getUserById(senderId);
        var attachmentsList = attachmentsMapper.toAttachmentsList(attachments);
        attachmentsRepository.saveAll(attachmentsList);
        Messages msg = new Messages();
        msg.setMessage(message);
        msg.setChatId(chat);
        msg.setSenderId(sender);
        for (var att : attachmentsList) {
            msg.addAttachments(att);
        }
        messageRepository.saveAndFlush(msg);
        return messageMapper.toMessageDto(msg);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, Long userId, boolean isAll) {
        Messages msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        User user = getUserById(userId);

        if (isAll && msg.getSenderId().getId().equals(userId)) {
            msg.setDelete(true);
        } else {
            msg.addUserDeleters(user);
        }
    }

    @Override
    @SneakyThrows
    public void sendMessage(WebSocketSession session, MessageToUser message) {
        session.sendMessage(new TextMessage(jsonMapper.writeValueAsString(message)));
    }

    @Override
    @Transactional
    public ChatIsCreated getChat(Long participantId, Long userId) {
        var chatIsCreated = new ChatIsCreated();
        AtomicBoolean isCreated = new AtomicBoolean(false);
        var chats = getChatsByParticipantId(participantId).stream()
                .filter(chat -> chat.getParticipants().contains(getUserById(userId)))
                .findFirst()
                .orElseGet(() -> {
                    isCreated.set(true);
                    return createChat(userId, participantId);
                });
        chatIsCreated.setChat(chats);
        chatIsCreated.setCreated(isCreated.get());
        return chatIsCreated;
    }

    @Transactional(readOnly = true)
    protected Chat getChatById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
    }

    @Transactional(readOnly = true)
    protected User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    protected Chat createChat(Long userId, Long participantId) {
        User owner = getUserById(userId);
        User participant = getUserById(participantId);

        Chat chat = new Chat();
        chat.setName(owner.getFirstName());
        chat.addParticipant(owner);
        chat.addParticipant(participant);

        return chatRepository.save(chat);
    }

    private ChatsDto mapChatToChatDto(Chat chat, User user) {
        ChatsDto chatDto = chatMapper.toChatDto(chat);

        List<MessageDto> messageDtos = messageRepository.findMessagesByChatId(chat).stream()
                .filter(msg -> !msg.isDelete()
                        && !msg.getUserDeleteMessage().stream().map(User::getId).toList().contains(user.getId()))
                .map(messageMapper::toMessageDto)
                .collect(Collectors.toList());
        chatDto.setMessages(messageDtos);
        return chatDto;
    }

    @Transactional(readOnly = true)
    protected List<Chat> getChatsByParticipantId(Long participantId) {
        User participant = getUserById(participantId);
        return chatRepository.findChatsByParticipantsContaining(participant);
    }
}
