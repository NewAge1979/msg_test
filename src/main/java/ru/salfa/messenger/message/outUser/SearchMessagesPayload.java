package ru.salfa.messenger.message.outUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.SearchMessagesResultsPayload;
import ru.salfa.messenger.service.ChatService;

import java.util.Map;

@Setter
@JsonTypeName(SearchMessagesPayload.ACTION)
public class SearchMessagesPayload extends MessageOutUser {
    public static final String ACTION = "search_messages";


    @JsonProperty("search_query")
    private String searchQuery;

    @NotBlank(message = "Chat ID cannot be null")
    @Pattern(regexp = "^[1-9]\\d*$", message = "Field must be a number greater than 0")
    @JsonProperty("chat_id")
    private String chatId;

    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    @SneakyThrows
    public void handler(ChatService service, Map<String, WebSocketSession> listeners, String userPhone) {
        var listMessageDto = service.searchMessage(Long.parseLong(chatId), searchQuery, userPhone);

        var searchResultPayload = new SearchMessagesResultsPayload();
        searchResultPayload.setMessages(listMessageDto);
        service.sendMessage(listeners.get(userPhone), searchResultPayload);
    }
}
