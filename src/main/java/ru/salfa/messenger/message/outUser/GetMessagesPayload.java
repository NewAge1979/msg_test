package ru.salfa.messenger.message.outUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.MessageResult;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.SearchMessagesResultsPayload;
import ru.salfa.messenger.service.ChatService;

import java.util.Map;

@Setter
@JsonTypeName(GetMessagesPayload.ACTION)
public class GetMessagesPayload extends MessageOutUser {
    public static final String ACTION = "get_messages";

    @NotBlank(message = "Chat ID cannot be null")
    @Pattern(regexp = "^[1-9]\\d*$", message = "Field must be a number greater than 0")
    @JsonProperty("chatId")
    private String chatId;

    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    @SneakyThrows
    public void handler(ChatService service, Map<String, WebSocketSession> listeners, String userPhone) {
        var listMessageDto = service.getMessageByChat(Long.parseLong(chatId), userPhone);

        var searchResultPayload = new SearchMessagesResultsPayload();
        searchResultPayload.setMessagesList(MessageResult.ofListMessageDto(listMessageDto));
        service.sendMessage(listeners.get(userPhone), searchResultPayload);
    }
}
