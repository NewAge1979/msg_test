package ru.salfa.messenger.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.message.outUser.*;
import ru.salfa.messenger.service.ChatService;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
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
        @JsonSubTypes.Type(value = DeleteMessagePayload.class, name = DeleteMessagePayload.ACTION),
        @JsonSubTypes.Type(value = SearchUserPayload.class, name = SearchUserPayload.ACTION),
        @JsonSubTypes.Type(value = BlockContactPayload.class, name = BlockContactPayload.ACTION),
        @JsonSubTypes.Type(value = GetMessagesPayload.class, name = GetMessagesPayload.ACTION),
        @JsonSubTypes.Type(value = AckMessagesPayload.class, name = AckMessagesPayload.ACTION)
})
public abstract class MessageOutUser {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String phoneNumber;
    private String action;

    @JsonIgnore
    public abstract void handler(ChatService service, Map<String, WebSocketSession> listeners, String userPhone);
}
