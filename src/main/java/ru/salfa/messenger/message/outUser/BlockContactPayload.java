package ru.salfa.messenger.message.outUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.SuccessActionPayload;
import ru.salfa.messenger.service.ChatService;

import java.util.Map;

@Setter
@JsonTypeName(BlockContactPayload.ACTION)
public class BlockContactPayload extends MessageOutUser {
    public static final String ACTION = "block_contact";

    @NotBlank(message = "User ID cannot be null")
    @Pattern(regexp = "^[1-9]\\d*$", message = "Field must be a number greater than 0")
    @JsonProperty("user_id")
    private String userId;

    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    @SneakyThrows
    public void handler(ChatService service, Map<String, WebSocketSession> listeners, String userPhone) {
        if (service.blockedContact(Long.valueOf(userId), userPhone)) {
            var payload = new SuccessActionPayload();
            payload.setMessage("User " + userId + " is blocked");
            service.sendMessage(listeners.get(userPhone), payload);
        }
    }
}
