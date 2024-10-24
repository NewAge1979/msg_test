package ru.salfa.messenger.message.outUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.service.ChatService;

import java.util.List;
import java.util.Map;

@Setter
@JsonTypeName(AckMessagesPayload.ACTION)
public class AckMessagesPayload extends MessageOutUser {
    public static final String ACTION = "ack_messages";


    @JsonProperty("messagesId")
    private List<Long> messagesId;

    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    @SneakyThrows
    public void handler(ChatService service, Map<String, WebSocketSession> listeners, String userPhone) {
        for (var messageId : messagesId) {
            service.ackMessage(messageId);
        }
    }
}
