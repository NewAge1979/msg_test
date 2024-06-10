package ru.salfa.messenger.message.outUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.ChatClearedPayload;
import ru.salfa.messenger.service.ChatService;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonTypeName(ClearChatPayload.ACTION)
public class ClearChatPayload extends MessageOutUser {
    public static final String ACTION = "clear_chat";

    @JsonProperty("chat_id")
    private Long chatId;

    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    @SneakyThrows
    public void handler(ChatService service, Map<Long, WebSocketSession> listeners, Long user) {
        var messagePayload = new ChatClearedPayload();
        messagePayload.setChatId(chatId);

        service.clearChat(chatId, user);
        service.sendMessage(listeners.get(user), messagePayload);
    }
}
