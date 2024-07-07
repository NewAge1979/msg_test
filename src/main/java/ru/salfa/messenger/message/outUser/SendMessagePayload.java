package ru.salfa.messenger.message.outUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.AttachmentsDto;
import ru.salfa.messenger.entity.User;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.ChatCreatedPayload;
import ru.salfa.messenger.message.toUser.ChatMessagePayload;
import ru.salfa.messenger.message.toUser.SuccessActionPayload;
import ru.salfa.messenger.service.ChatService;

import java.util.List;
import java.util.Map;

@Setter
@JsonTypeName(SendMessagePayload.ACTION)
public class SendMessagePayload extends MessageOutUser {
    public static final String ACTION = "send_message";

    private String message;
    @JsonProperty("participant_id")
    private Long participantId;
    private List<AttachmentsDto> attachments;

    @Override
    public String getAction() {
        return ACTION;
    }

    @SneakyThrows
    @Override
    public void handler(ChatService service, Map<String, WebSocketSession> listeners, String userPhone) {

        var chatIsCreated = service.getOrCreateChat(participantId, userPhone);
        var chat = chatIsCreated.getChat();
        var participantPhoneList = chat.getParticipants().stream().map(User::getPhone)
                .filter(phone -> !phone.equals(userPhone)).toList();

        if (chatIsCreated.isCreated()) {
            var creatChatPayload = new ChatCreatedPayload();
            creatChatPayload.setChatId(chat.getId());
            creatChatPayload.setSender(chat.getParticipants().stream()
                    .filter(user -> user.getPhone().equals(userPhone)).findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId());
            for (var participantPhone : participantPhoneList) {
                if (listeners.containsKey(participantPhone)) {
                    service.sendMessage(listeners.get(participantPhone), creatChatPayload);
                }
            }

        }

        var successPayload = new SuccessActionPayload();
        var messagePayload = new ChatMessagePayload();

        var msgDto = service.createAndSaveMsg(chat.getId(), userPhone, message, attachments);
        messagePayload.setMessages(msgDto);
        messagePayload.setChatId(chat.getId());


        for (var participantPhone : participantPhoneList) {
            if (listeners.containsKey(participantPhone)) {
                service.sendMessage(listeners.get(participantPhone), messagePayload);
            }
        }
        successPayload.setMessage("Message successfully");
        successPayload.setChatId(chat.getId());
        successPayload.setCreated(chatIsCreated.isCreated());
        service.sendMessage(listeners.get(userPhone), successPayload);
    }
}
