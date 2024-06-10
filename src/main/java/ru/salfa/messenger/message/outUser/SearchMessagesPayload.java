package ru.salfa.messenger.message.outUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.SearchResultsPayload;
import ru.salfa.messenger.service.ChatService;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName(SearchMessagesPayload.ACTION)
@ToString
public class SearchMessagesPayload extends MessageOutUser {
    public static final String ACTION = "search_messages";

    @JsonProperty("search_query")
    private String searchQuery;
    @JsonProperty("chat_id")
    private Long chatId;
    private String token;
    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    @SneakyThrows
    public void handler(ChatService service, Map<Long, WebSocketSession> listeners, Long user) {
        var listMessageDto = service.searchMessage(chatId,searchQuery, user);

        var searchResultPayload = new SearchResultsPayload();
        searchResultPayload.setMessages(listMessageDto);
        service.sendMessage(listeners.get(user), searchResultPayload);
    }
}
