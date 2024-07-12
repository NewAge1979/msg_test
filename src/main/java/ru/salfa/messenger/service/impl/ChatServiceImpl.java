package ru.salfa.messenger.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.*;
import ru.salfa.messenger.entity.Chat;
import ru.salfa.messenger.entity.Messages;
import ru.salfa.messenger.entity.User;
import ru.salfa.messenger.exception.ChatNotFoundException;
import ru.salfa.messenger.exception.MessageNotFoundException;
import ru.salfa.messenger.exception.UserNotFoundException;
import ru.salfa.messenger.message.MessageToUser;
import ru.salfa.messenger.repository.AttachmentsRepository;
import ru.salfa.messenger.repository.ChatRepository;
import ru.salfa.messenger.repository.MessageRepository;
import ru.salfa.messenger.repository.UserRepository;
import ru.salfa.messenger.service.ChatService;
import ru.salfa.messenger.utils.mapper.AttachmentsMapper;
import ru.salfa.messenger.utils.mapper.ChatMapper;
import ru.salfa.messenger.utils.mapper.MessageMapper;
import ru.salfa.messenger.utils.mapper.UserMapper;

import java.io.IOException;
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
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ChatsDto> getListChatDtoByUserPhone(String phone) {
        return getChatsByParticipantPhone(phone).stream()
                .map(chat -> mapChatToChatDto(chat, phone))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> searchMessage(Long chatId, String query, String userPhone) {

        return messageRepository.findAll().stream()
                .filter(msg -> msg.getChatId().getId().equals(chatId))
                .filter(msg -> !msg.isDelete())
                .filter(msg -> !msg.getUserDeleteMessage().stream().map(User::getPhone).toList().contains(userPhone))
                .filter(msg -> msg.getMessage().toLowerCase(Locale.ROOT).contains(query.toLowerCase()))
                .map(messageMapper::toMessageDto)
                .toList();
    }

    @Override
    @Transactional
    public boolean clearChat(Long chatId, String userPhone) {
        var msgList = messageRepository.findAll().stream()
                .filter(msg -> msg.getChatId().getId().equals(chatId)).toList();
        if (msgList.isEmpty()) {
            return false;
        }
        User user = getUserByPhone(userPhone);
        msgList.forEach(msg -> msg.addUserDeleters(user));
        return true;
    }

    @Override
    @Transactional
    public MessageDto createAndSaveMsg(Long chatId, String senderPhone, String message, List<AttachmentsDto> attachments) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        User sender = getUserByPhone(senderPhone);
        Messages msg = new Messages();
        msg.setMessage(message);
        msg.setChatId(chat);
        msg.setSenderId(sender);

        if (attachments != null && !attachments.isEmpty()) {
            var attachmentsList = attachmentsMapper.toAttachmentsList(attachments);
            attachmentsRepository.saveAll(attachmentsList);
            for (var att : attachmentsList) {
                msg.addAttachments(att);
            }
        }

        messageRepository.save(msg);
        return messageMapper.toMessageDto(msg);
    }

    @Override
    @Transactional
    public boolean deleteMessage(Long messageId, String userPhone, boolean isAll) {
        Messages msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));
        User user = getUserByPhone(userPhone);

        if (isAll && msg.getSenderId().getId().equals(user.getId())) {
            msg.setDelete(true);
        } else {
            msg.addUserDeleters(user);
        }
        return true;
    }

    @Override
    public boolean sendMessage(WebSocketSession session, MessageToUser message) throws IOException {
        session.sendMessage(new TextMessage(jsonMapper.writeValueAsString(message)));
        return true;
    }

    @Override
    @Transactional
    public ChatIsCreated getOrCreateChat(Long participantId, String userPhone) {
        if (!userIsBlocked(userPhone, participantId)) {
            var chatIsCreated = new ChatIsCreated();
            AtomicBoolean isCreated = new AtomicBoolean(false);
            var chats = getChatsByParticipantId(participantId).stream()
                    .filter(chat -> chat.getParticipants().stream().map(User::getPhone)
                            .toList().contains(userPhone))
                    .findFirst()
                    .orElseGet(() -> {
                        isCreated.set(true);
                        return createChat(userPhone, participantId);
                    });
            chatIsCreated.setChat(chats);
            chatIsCreated.setCreated(isCreated.get());
            return chatIsCreated;
        } else
            throw new RuntimeException("we are blocked by user");

    }

    private boolean userIsBlocked(String userPhone, Long participantId) {
        var user = getUserByPhone(userPhone);

        return userRepository.findByIdAndIsDeleted(participantId, false)
                .orElseThrow(() -> new UserNotFoundException("User not found"))
                .getBlockedContacts().contains(user);
    }

    @Override
    public List<UserDto> getListUserDtoByNickname(String nickname) {
        return userMapper.toUserDtos(userRepository.findByNicknameContainingIgnoreCaseAndIsDeleted(nickname, false));
    }

    @Override
    @Transactional
    public boolean blockedContact(Long userId, String phone) {
        var blockedUser = userRepository.findByIdAndIsDeleted(userId, false)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        var user = getUserByPhone(phone);
        if (user.isBlockedContact(blockedUser)) {
            user.unblockContact(blockedUser);
            return true;
        }
        user.blockContact(blockedUser);
        return true;
    }

    private User getUserByPhone(String phone) {
        return userRepository.findByPhoneAndIsDeleted(phone, false)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    protected Chat createChat(String userPhone, Long participantId) {
        User owner = getUserByPhone(userPhone);

        User participant = userRepository.findByIdAndIsDeleted(participantId, false)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Chat chat = new Chat();
        chat.setName(owner.getFirstName());
        chat.addParticipant(owner);
        chat.addParticipant(participant);

        return chatRepository.save(chat);
    }

    private List<Chat> getChatsByParticipantPhone(String phone) {
        return chatRepository.findAll().stream()
                .filter(chat -> chat.getParticipants()
                        .stream().map(User::getPhone)
                        .toList().contains(phone))
                .toList();
    }

    private List<Chat> getChatsByParticipantId(Long id) {
        return chatRepository.findAll().stream()
                .filter(chat -> chat.getParticipants()
                        .stream().map(User::getId)
                        .toList().contains(id))
                .toList();
    }

    private ChatsDto mapChatToChatDto(Chat chat, String phone) {
        ChatsDto chatDto = chatMapper.toChatDto(chat);
        List<MessageDto> messageDtos = messageRepository.findMessagesByChatId(chat).stream()
                .filter(msg -> !msg.isDelete()
                        && !msg.getUserDeleteMessage().stream().map(User::getPhone)
                        .toList().contains(phone))
                .map(messageMapper::toMessageDto)
                .collect(Collectors.toList());
        chatDto.setMessages(messageDtos);
        return chatDto;
    }
}
