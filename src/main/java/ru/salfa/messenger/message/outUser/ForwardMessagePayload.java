package ru.salfa.messenger.message.outUser;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.AttachmentsDto;
import ru.salfa.messenger.dto.model.MessageDto;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.ChatMessagePayload;
import ru.salfa.messenger.message.toUser.SuccessForwardedPayload;
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
    public void handler(ChatService service, Map<Long, WebSocketSession> listeners, Long user) {
        var successPayload = new SuccessForwardedPayload();

        var chatIsCreated = service.getChat(participantId, user);
        var chatId = chatIsCreated.getChat().getId();
        successPayload.setCreated(chatIsCreated.isCreated());
        successPayload.setChatId(chatId);

        var forwardMessage = service.createAndSaveMsg(chatId, user, forwardedMessage, attachmentsForwardedMessage);
        var msg = service.createAndSaveMsg(chatId, user, message, attachmentsMessage);

        var forwardMessagePayload = creatMsgPayload(chatId, forwardMessage);
        forwardMessagePayload.setOriginalSender(originalSender);

        var messagePayload = creatMsgPayload(chatId, msg);
        messagePayload.setOriginalSender(null);

        service.sendMessage(listeners.get(user), successPayload);
        if (listeners.containsKey(participantId)) {
            service.sendMessage(listeners.get(participantId), forwardMessagePayload);
            service.sendMessage(listeners.get(participantId), messagePayload);
        }
    }

    private ChatMessagePayload creatMsgPayload(Long chatId, MessageDto forwardMessage) {
        var msgPayload = new ChatMessagePayload();
        msgPayload.setChatId(chatId);
        msgPayload.setMessage(forwardMessage);
        return msgPayload;
    }
}