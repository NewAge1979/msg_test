package ru.salfa.messenger.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.ChatIsCreated;
import ru.salfa.messenger.dto.model.ChatsDto;
import ru.salfa.messenger.dto.model.Document;
import ru.salfa.messenger.dto.model.MessageDto;
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
import ru.salfa.messenger.service.impl.ChatServiceImpl;
import ru.salfa.messenger.utils.mapper.ChatMapper;
import ru.salfa.messenger.utils.mapper.MessageMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatServiceImplTest {
    private static final String phone = "1234567890";
    private static final Long defaultId = 1L;
    private static User user;
    private static Chat chat;

    @Mock
    private AttachmentsRepository attachmentsRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private ChatMapper chatMapper;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    @InjectMocks
    private ChatServiceImpl chatService;

    @BeforeAll
    static void setAll() {
        user = createUser();
        chat = createChat(user);
    }

    private static User createUser() {
        User user = new User();
        user.setId(ChatServiceImplTest.defaultId);
        user.setPhone(ChatServiceImplTest.phone);
        return user;
    }

    private static Chat createChat(User... participants) {
        Chat chat = new Chat();
        chat.setId(ChatServiceImplTest.defaultId);
        chat.setParticipants(Arrays.asList(participants));
        return chat;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Messages createMessage(Chat chat, User sender, boolean isDeleted) {
        Messages message = new Messages();
        message.setId(ChatServiceImplTest.defaultId);
        message.setMessage("hello world");
        message.setChatId(chat);
        message.setSenderId(sender);
        message.setDelete(isDeleted);
        return message;
    }

    @Test
    void testGetListChatDtoByUserPhone_ValidPhone() {
        // given
        ChatsDto chatDto = new ChatsDto();
        when(chatRepository.findAll()).thenReturn(List.of(chat));
        when(chatMapper.toChatDto(any(Chat.class))).thenReturn(chatDto);

        // when
        List<ChatsDto> result = chatService.getListChatDtoByUserPhone(phone);

        //then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetListChatDtoByUserPhone_InvalidPhone() {
        // given
        String phone = "invalid";
        when(chatRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<ChatsDto> result = chatService.getListChatDtoByUserPhone(phone);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void testSearchMessage_ValidParametersWithMatches() {
        // given
        String query = "hello";
        Messages message = createMessage(chat, user, false);
        MessageDto messageDto = new MessageDto();
        when(messageRepository.findAll()).thenReturn(List.of(message));
        when(messageMapper.toMessageDto(any(Messages.class))).thenReturn(messageDto);

        // when
        List<MessageDto> result = chatService.searchMessage(defaultId, query, phone);

        // then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testSearchMessage_ValidParametersNoMatches() {
        // given
        String query = "goodbye";
        Messages message = createMessage(chat, user, false);
        when(messageRepository.findAll()).thenReturn(List.of(message));
        // when
        List<MessageDto> result = chatService.searchMessage(defaultId, query, phone);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchMessage_ValidParametersWithDeletedMessages() {
        //given
        String query = "hello";
        Messages message = createMessage(chat, user, true);
        when(messageRepository.findAll()).thenReturn(List.of(message));

        // when
        List<MessageDto> result = chatService.searchMessage(defaultId, query, phone);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchMessage_InvalidUserPhone() {
        // given
        String query = "hello";
        Messages message = createMessage(chat, user, false);
        when(messageRepository.findAll()).thenReturn(List.of(message));

        // when
        List<MessageDto> result = chatService.searchMessage(defaultId, query, "invalid");

        // then
        assertNotNull(result);
        result.forEach(Assertions::assertNull);
    }

    @Test
    void testSearchMessage_InvalidChatId() {
        // given
        String query = "hello";
        Messages message = createMessage(chat, user, false);
        when(messageRepository.findAll()).thenReturn(List.of(message));

        // when
        List<MessageDto> result = chatService.searchMessage(2L, query, phone);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void testClearChat_ValidParameters() {
        // given
        Messages message = createMessage(chat, user, false);
        when(messageRepository.findAll()).thenReturn(List.of(message));
        when(userRepository.findByPhoneAndIsDeleted(phone, false)).thenReturn(Optional.of(user));

        // when
        var result = chatService.clearChat(defaultId, phone);

        // then
        assertTrue(message.getUserDeleteMessage().contains(user));
        assertTrue(result);
    }

    @Test
    void testClearChat_InvalidChatId() {
        // given
        when(chatRepository.findById(defaultId)).thenReturn(Optional.empty());

        // when
        var result = chatService.clearChat(defaultId, phone);

        // then
        assertFalse(result);
    }

    @Test
    void testSendMessageToChat() throws IOException {
        // given
        WebSocketSession session = mock(WebSocketSession.class);
        MessageToUser message = new MessageToUser();

        // when
        var result = chatService.sendMessage(session, message);

        // then
        assertTrue(result);
        verify(session).sendMessage(any(TextMessage.class));
    }

    @Test
    void testSendMessageToChat_SessionException() throws IOException {
        // given
        WebSocketSession session = mock(WebSocketSession.class);
        MessageToUser message = new MessageToUser();
        doThrow(IOException.class).when(session).sendMessage(any(TextMessage.class));

        // when then
        assertThrows(IOException.class, () -> chatService.sendMessage(session, message));
    }

    @Test
    void testCreateAndSaveMsg_Success() {
        //given
        String messageText = "Hello";
        List<Document> attachments = List.of(new Document());
        MessageDto messageDto = new MessageDto();
        when(chatRepository.findById(defaultId)).thenReturn(Optional.of(chat));
        when(userRepository.findByPhoneAndIsDeleted(phone, false)).thenReturn(Optional.of(user));
        when(messageMapper.toMessageDto(any(Messages.class))).thenReturn(messageDto);

        // when
        MessageDto result = chatService.createAndSaveMsg(defaultId, phone, messageText, attachments);

        //then
        assertNotNull(result);
        assertEquals(messageDto, result);
    }

    @Test
    void testCreateAndSaveMsg_ChatNotFound() {
        // given
        String messageText = "Hello";
        List<Document> attachments = List.of(new Document());
        when(chatRepository.findById(defaultId)).thenReturn(Optional.empty());

        // when then
        assertThrows(ChatNotFoundException.class, () -> chatService.createAndSaveMsg(defaultId, phone, messageText, attachments));
    }

    @Test
    void testCreateAndSaveMsg_UserNotFound() {
        // given
        String messageText = "Hello";
        List<Document> attachments = List.of(new Document());
        when(chatRepository.findById(defaultId)).thenReturn(Optional.of(chat));
        when(userRepository.findByPhoneAndIsDeleted(phone, false)).thenReturn(Optional.empty());

        // when then
        assertThrows(UserNotFoundException.class, () -> chatService.createAndSaveMsg(defaultId, phone, messageText, attachments));
    }

    @Test
    void testDeleteMessage_Success_AllMessages() {
        // given
        boolean isAll = true;
        Messages message = new Messages();
        message.setSenderId(user);
        when(messageRepository.findById(defaultId)).thenReturn(Optional.of(message));
        when(userRepository.findByPhoneAndIsDeleted(phone, false)).thenReturn(Optional.of(user));

        // when
        boolean result = chatService.deleteMessage(defaultId, phone, isAll);

        // then
        assertTrue(result);
        assertTrue(message.isDelete());
        assertFalse(message.getUserDeleteMessage().contains(user));
    }

    @Test
    void testDeleteMessage_Success_AllMessages_UserNotSender() {
        // given
        boolean isAll = true;
        Messages message = new Messages();
        User sender = new User();
        sender.setId(2L);
        message.setSenderId(sender);
        when(messageRepository.findById(defaultId)).thenReturn(Optional.of(message));
        when(userRepository.findByPhoneAndIsDeleted(phone, false)).thenReturn(Optional.of(user));

        // when
        boolean result = chatService.deleteMessage(defaultId, phone, isAll);

        // then
        assertTrue(result);
        assertTrue(message.getUserDeleteMessage().contains(user));
        assertFalse(message.isDelete());
    }

    @Test
    void testDeleteMessage_Success_SingleMessage() {
        // given
        boolean isAll = false;
        Messages message = new Messages();
        when(messageRepository.findById(defaultId)).thenReturn(Optional.of(message));
        when(userRepository.findByPhoneAndIsDeleted(phone, false)).thenReturn(Optional.of(user));

        // when
        boolean result = chatService.deleteMessage(defaultId, phone, isAll);

        // then
        assertTrue(result);
        assertFalse(message.isDelete());
        assertTrue(message.getUserDeleteMessage().contains(user));
    }

    @Test
    void testDeleteMessage_MessageNotFound() {
        // given
        boolean isAll = true;
        when(messageRepository.findById(defaultId)).thenReturn(Optional.empty());

        // when then
        assertThrows(MessageNotFoundException.class, () -> chatService.deleteMessage(defaultId, phone, isAll));
    }

    @Test
    void testDeleteMessage_UserNotFound() {
        // given
        boolean isAll = true;
        Messages message = new Messages();
        when(messageRepository.findById(defaultId)).thenReturn(Optional.of(message));
        when(userRepository.findByPhoneAndIsDeleted(phone, false)).thenReturn(Optional.empty());

        // when then
        assertThrows(UserNotFoundException.class, () -> chatService.deleteMessage(defaultId, phone, isAll));
    }

    @Test
    void testGetOrCreateChat_ExistingChat() {
        // given
        when(userRepository.findByPhoneAndIsDeleted(phone, false)).thenReturn(Optional.of(user));
        when(userRepository.findByIdAndIsDeleted(defaultId, false)).thenReturn(Optional.of(new User()));
        when(chatRepository.findAll()).thenReturn(List.of(chat));

        // when
        ChatIsCreated result = chatService.getOrCreateChat(defaultId, phone);

        // then
        assertNotNull(result);
        assertFalse(result.isCreated());
        assertEquals(chat, result.getChat());
    }

    @Test
    void testGetOrCreateChat_NewChat() {
        // given
        when(userRepository.findByPhoneAndIsDeleted(phone, false)).thenReturn(Optional.of(user));
        long participantId = 2L;
        when(userRepository.findByIdAndIsDeleted(participantId, false)).thenReturn(Optional.of(new User()));
        when(chatRepository.findAll()).thenReturn(List.of());
        when(chatRepository.save(any(Chat.class))).thenReturn(chat);

        // when
        ChatIsCreated result = chatService.getOrCreateChat(participantId, phone);

        // then
        assertNotNull(result);
        assertTrue(result.isCreated());
        assertEquals(chat, result.getChat());
    }

    @Test
    void testGetOrCreateChat_UserNotFound() {
        // given
        when(userRepository.findByPhoneAndIsDeleted(phone, false)).thenReturn(Optional.empty());

        // when then
        assertThrows(UserNotFoundException.class, () -> chatService.getOrCreateChat(defaultId, phone));
    }
}
