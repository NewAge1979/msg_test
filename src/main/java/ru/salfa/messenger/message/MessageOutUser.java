package ru.salfa.messenger.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.message.outUser.*;
import ru.salfa.messenger.service.ChatService;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "action"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SendMessagePayload.class, name = SendMessagePayload.ACTION),
        @JsonSubTypes.Type(value = ClearChatPayload.class, name = ClearChatPayload.ACTION),
        @JsonSubTypes.Type(value = SearchMessagesPayload.class, name = SearchMessagesPayload.ACTION),
        @JsonSubTypes.Type(value = ForwardMessagePayload.class, name = ForwardMessagePayload.ACTION),
        @JsonSubTypes.Type(value = DeleteMessagePayload.class, name = DeleteMessagePayload.ACTION)
})
public abstract class MessageOutUser {

    private String action;

    @JsonIgnore
    public abstract void handler(ChatService service, Map<String, WebSocketSession> listeners, String userPhone) ;
}
