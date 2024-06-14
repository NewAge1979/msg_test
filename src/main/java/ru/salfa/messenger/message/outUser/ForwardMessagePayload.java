package ru.salfa.messenger.message.outUser;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.AttachmentsDto;
import ru.salfa.messenger.dto.model.MessageDto;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.ChatCreatedPayload;
import ru.salfa.messenger.message.toUser.ChatMessagePayload;
import ru.salfa.messenger.message.toUser.SuccessSendPayload;
import ru.salfa.messenger.service.ChatService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonTypeName(ForwardMessagePayload.ACTION)
public class ForwardMessagePayload extends MessageOutUser {
    public static final String ACTION = "forward_message";
    @JsonIgnore
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

    @JsonProperty("participant_id")
    private Long participantId;
    private String message;
    @JsonProperty("attachments_message")
    private List<AttachmentsDto> attachmentsMessage;
    @JsonProperty("forwarded_message")
    private String forwardedMessage;
    @JsonProperty("attachments_forwarded_message")
    private List<AttachmentsDto> attachmentsForwardedMessage;
    @JsonProperty("original_sender")
    private String originalSender;

    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    @SneakyThrows
    public void handler(ChatService service, Map<String, WebSocketSession> listeners, String userPhone) {
        var successPayload = new SuccessSendPayload();
        var chatIsCreated = service.getOrCreateChat(participantId, userPhone);
        var participantPhone = chatIsCreated.getChat().getParticipants()
                .stream().filter(p -> p.getId().equals(participantId)).findFirst()
                .orElseThrow(() -> new RuntimeException("User not found")).getPhone();
        var chat = chatIsCreated.getChat();
        var chatId = chat.getId();

        if (chatIsCreated.isCreated()) {
            var creatChatPayload = new ChatCreatedPayload();
            creatChatPayload.setChatId(chatId);
            creatChatPayload.setSender(chat.getParticipants().stream()
                    .filter(user -> user.getPhone().equals(userPhone)).findFirst()
                    .orElseThrow(()->new RuntimeException("User not found"))
                    .getId());

            if (listeners.containsKey(participantPhone)) {
                service.sendMessage(listeners.get(participantPhone), creatChatPayload);
            }

        }


        successPayload.setCreated(chatIsCreated.isCreated());
        successPayload.setChatId(chatId);

        var forwardMessage = service.createAndSaveMsg(chatId, userPhone, forwardedMessage, attachmentsForwardedMessage);
        var msg = service.createAndSaveMsg(chatId, userPhone, message, attachmentsMessage);

        var forwardMessagePayload = creatMsgPayload(chatId, forwardMessage, originalSender);

        var messagePayload = creatMsgPayload(chatId, msg);

        service.sendMessage(listeners.get(userPhone), successPayload);
        if (listeners.containsKey(participantPhone)) {
            service.sendMessage(listeners.get(participantPhone), forwardMessagePayload);
            service.sendMessage(listeners.get(participantPhone), messagePayload);
        }
    }

    private ChatMessagePayload creatMsgPayload(Long chatId, MessageDto forwardMessage) {
        var msgPayload = new ChatMessagePayload();
        msgPayload.setChatId(chatId);
        msgPayload.setMessages(forwardMessage);
        msgPayload.setOriginalSender(null);
        return msgPayload;
    }

    private ChatMessagePayload creatMsgPayload(Long chatId, MessageDto forwardMessage, String originalSender) {
        var msgPayload = new ChatMessagePayload();
        msgPayload.setChatId(chatId);
        msgPayload.setMessages(forwardMessage);
        msgPayload.setOriginalSender(originalSender);
        return msgPayload;
    }
}