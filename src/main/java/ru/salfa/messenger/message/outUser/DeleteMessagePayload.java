package ru.salfa.messenger.message.outUser;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.MessageDeletedPayload;
import ru.salfa.messenger.service.ChatService;

import java.util.Map;

@Setter
@JsonTypeName(DeleteMessagePayload.ACTION)
public class DeleteMessagePayload extends MessageOutUser {
    public static final String ACTION = "delete_message";

    @NotBlank(message = "Chat ID cannot be null")
    @Pattern(regexp = "^[1-9]\\d*$", message = "Field must be a number greater than 0")
    @JsonProperty("chat_id")
    private String chatId;

    @NotBlank(message = "Message ID cannot be null")
    @Pattern(regexp = "^[1-9]\\d*$", message = "Field must be a number greater than 0")
    @JsonProperty("message_id")
    private String inputMessageId;

    @NotBlank(message = "Message ID cannot be null")
    @Pattern(regexp = "^(true|false)$", message = "Field must be 'true' or 'false'")
    @JsonProperty("delete_for_everyone")
    private String deleteForEveryOne;

    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    @SneakyThrows
    public void handler(ChatService service, Map<String, WebSocketSession> listeners, String phone) {
        var messagePayload = new MessageDeletedPayload();
        var messageId = Long.parseLong(inputMessageId);

        messagePayload.setChatId(Long.parseLong(chatId));
        messagePayload.setMessageId(messageId);
        service.deleteMessage(messageId, phone, Boolean.parseBoolean(deleteForEveryOne));
        service.sendMessage(listeners.get(phone), messagePayload);
    }
}
