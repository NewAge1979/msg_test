package ru.salfa.messenger.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.*;
import ru.salfa.messenger.entity.enums.MessageType;
import ru.salfa.messenger.entity.postgres.Attachments;
import ru.salfa.messenger.entity.postgres.Chat;
import ru.salfa.messenger.entity.postgres.Messages;
import ru.salfa.messenger.entity.postgres.User;
import ru.salfa.messenger.exception.ChatNotFoundException;
import ru.salfa.messenger.exception.MessageNotFoundException;
import ru.salfa.messenger.exception.UserNotFoundException;
import ru.salfa.messenger.message.MessageToUser;
import ru.salfa.messenger.repository.AttachmentsRepository;
import ru.salfa.messenger.repository.ChatRepository;
import ru.salfa.messenger.repository.MessageRepository;
import ru.salfa.messenger.repository.UserRepository;
import ru.salfa.messenger.service.ChatService;
import ru.salfa.messenger.service.FileService;
import ru.salfa.messenger.utils.mapper.ChatMapper;
import ru.salfa.messenger.utils.mapper.MessageMapper;
import ru.salfa.messenger.utils.mapper.UserMapper;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final UserMapper userMapper;
    private final FileService fileService;

    @Override
    @Transactional(readOnly = true)
    public List<ChatsDto> getListChatDtoByUserPhone(String phone, Map<String, WebSocketSession> listeners) {
        return getChatsByParticipantPhone(phone).stream()
                .map(chat -> mapChatToChatDto(chat, phone, listeners))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> searchMessage(Long chatId, String query, String userPhone) {
        var userId = userRepository.findByPhoneAndIsDeleted(userPhone, false).orElseThrow().getId();
        return messageRepository.findAll().stream()
                .filter(msg -> msg.getChatId().getId().equals(chatId))
                .filter(msg -> !msg.isDelete())
                .filter(msg -> !msg.getUserDeleteMessage().stream().map(User::getPhone).toList().contains(userPhone))
                .filter(msg -> msg.getMessage().toLowerCase(Locale.ROOT).contains(query.toLowerCase()))
                .sorted(Comparator.comparing(Messages::getCreated).reversed())
                .map(messages -> messageMapper.toMessageDto(messages, userId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getMessageByChat(Long chatId, String userPhone) {
        var userId = userRepository.findByPhoneAndIsDeleted(userPhone, false).orElseThrow().getId();
        return messageRepository.findAll().stream()
                .filter(msg -> msg.getChatId().getId().equals(chatId))
                .filter(msg -> !msg.isDelete())
                .filter(msg -> !msg.getUserDeleteMessage().stream().map(User::getPhone).toList().contains(userPhone))
                .sorted(Comparator.comparing(Messages::getCreated).reversed())
                .map(messages -> messageMapper.toMessageDto(messages, userId))
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
    public MessageDto createAndSaveMsg(Long chatId, String senderPhone, String message, List<Document> documents) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        User sender = getUserByPhone(senderPhone);
        Messages msg = new Messages();
        msg.setMessage(message);
        msg.setChatId(chat);
        msg.setSenderId(sender);
        msg.setType(MessageType.TEXT);

        if (documents != null && !documents.isEmpty()) {
            documents.forEach(document -> {
                        MessageDigest digest;
                        try {
                            digest = MessageDigest.getInstance("SHA-256");
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                        var data = Base64.getDecoder().decode(document.getData());

                        byte[] hash = digest.digest(data);
                        String hashString = Base64.getEncoder().encodeToString(hash);

                        var url = fileService.save(hashString, data, document.getName());
                        var attachment = new Attachments();
                        attachment.setUrl(url);
                        attachment.setFilename(document.getName());
                        attachment.setType(document.getType());
                        attachmentsRepository.save(attachment);
                        msg.addAttachments(attachment);
                    }
            );

        }

        messageRepository.save(msg);
        return messageMapper.toMessageDto(msg, sender.getId());
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
    public List<UserDto> getListUserDtoByPhone(String phone) {
        return List.of(userMapper.toUserDto(userRepository.findByPhoneAndIsDeleted(phone, false)
                .orElseThrow(() -> new UserNotFoundException(String.format("User by phone number %s not found", phone)))));
    }

    @Override
    public void disconnectUser(String phoneNumber) {
        var user = userRepository.findByPhoneAndIsDeleted(phoneNumber, false).orElseThrow();
        user.setLastLogin(OffsetDateTime.now());
        userRepository.saveAndFlush(user);
    }

    @Override
    public void ackMessage(Long messageID) {
        var message = messageRepository.findById(messageID).orElseThrow(() -> new MessageNotFoundException("Message not found"));
        message.setIsRead(true);
        messageRepository.saveAndFlush(message);
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

    private ChatsDto mapChatToChatDto(Chat chat, String phone, Map<String, WebSocketSession> listeners) {
        ChatsDto chatDto = chatMapper.toChatDto(chat);

        Long unreadMessageCount = messageRepository.findMessagesByChatId(chat).stream()
                .filter(msg -> !msg.isDelete()
                        && !msg.getUserDeleteMessage().stream().map(User::getPhone)
                        .toList().contains(phone))
                .filter(messages -> !messages.getIsRead()).count();

        var participant = chat.getParticipants().stream().filter(user -> !user.getPhone().equals(phone)).findFirst().orElseThrow();
        chatDto.setAvatar(participant.getAvatar());
        chatDto.setUnreadMessages(unreadMessageCount);
        chatDto.setOnlineStatus(listeners.containsKey(participant.getPhone()) ? "online" : participant.getLastLogin().format(DateTimeFormatter.ISO_DATE));
        return chatDto;
    }
}
