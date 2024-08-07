package ru.salfa.messenger.message.outUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.Document;
import ru.salfa.messenger.entity.postgres.User;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.ChatCreatedPayload;
import ru.salfa.messenger.message.toUser.ChatMessagePayload;
import ru.salfa.messenger.message.toUser.SuccessActionPayload;
import ru.salfa.messenger.service.ChatService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Setter
@JsonTypeName(SendMessagePayload.ACTION)
@Slf4j
public class SendMessagePayload extends MessageOutUser {
    public static final String ACTION = "send_message";

    @NotBlank(message = "Message cannot be null")
    private String message;

    @NotBlank(message = "Participant ID cannot be null")
    @Pattern(regexp = "^[1-9]\\d*$", message = "Field must be a number greater than 0")
    @JsonProperty("participant_id")
    private String participantId;

    private List<Document> documents;

    @Override
    public String getAction() {
        return ACTION;
    }


    @Override
    public void handler(ChatService service, Map<String, WebSocketSession> listeners, String userPhone) {
        log.info("Send message to user {}", userPhone);
        var chatIsCreated = service.getOrCreateChat(Long.parseLong(participantId), userPhone);
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
                    try {
                        service.sendMessage(listeners.get(participantPhone), creatChatPayload);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        return;
                    }

                }
            }

        }

        var successPayload = new SuccessActionPayload();
        var messagePayload = new ChatMessagePayload();
        try {
            var msgDto = service.createAndSaveMsg(chat.getId(), userPhone, message, documents);
            messagePayload.setMessages(msgDto);
            messagePayload.setChatId(chat.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }


        for (var participantPhone : participantPhoneList) {
            if (listeners.containsKey(participantPhone)) {
                try {
                    service.sendMessage(listeners.get(participantPhone), messagePayload);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        successPayload.setMessage("Message successfully");
        successPayload.setChatId(chat.getId());
        successPayload.setCreated(chatIsCreated.isCreated());
        try {
            service.sendMessage(listeners.get(userPhone), successPayload);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
