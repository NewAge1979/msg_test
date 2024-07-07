package ru.salfa.messenger.message.outUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.SuccessActionPayload;
import ru.salfa.messenger.service.ChatService;

import java.util.Map;

@Setter
@JsonTypeName(BlockContactPayload.ACTION)
public class BlockContactPayload extends MessageOutUser {
    public static final String ACTION = "block_contact";
    @JsonProperty("user_id")
    private Long userId;
    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    @SneakyThrows
    public void handler(ChatService service, Map<String, WebSocketSession> listeners, String userPhone) {
        if(service.blockedContact(userId, userPhone)) {
            var payload = new SuccessActionPayload();
            payload.setMessage("User " + userId + " is blocked");
            service.sendMessage(listeners.get(userPhone), payload);
        }
    }
}
