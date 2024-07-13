package ru.salfa.messenger.message.outUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.ChatClearedPayload;
import ru.salfa.messenger.service.ChatService;

import java.util.Map;

@Setter
@JsonTypeName(ClearChatPayload.ACTION)
public class ClearChatPayload extends MessageOutUser {
    public static final String ACTION = "clear_chat";

    @NotBlank(message = "Chat ID cannot be null")
    @Pattern(regexp = "^[1-9]\\d*$", message = "Field must be a number greater than 0")
    @JsonProperty("chat_id")
    private String inputChatId;

    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    @SneakyThrows
    public void handler(ChatService service, Map<String, WebSocketSession> listeners, String userPhone) {
        var chatId = Long.parseLong(inputChatId);
        var messagePayload = new ChatClearedPayload();
        messagePayload.setChatId(chatId);
        service.clearChat(chatId, userPhone);
        service.sendMessage(listeners.get(userPhone), messagePayload);
    }
}
