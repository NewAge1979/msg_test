package ru.salfa.messenger.message.outUser;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.MessageDeletedPayload;
import ru.salfa.messenger.service.ChatService;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonTypeName(DeleteMessagePayload.ACTION)
public class DeleteMessagePayload extends MessageOutUser {
    public static final String ACTION = "delete_message";

    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("message_id")
    private Long messageId;
    @JsonProperty("delete_for_everyone")
    private boolean deleteForEveryOne;

    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    @SneakyThrows
    public void handler(ChatService service, Map<String, WebSocketSession> listeners, String phone) {
        var messagePayload = new MessageDeletedPayload();

        messagePayload.setChatId(chatId);
        messagePayload.setMessageId(messageId);
        service.deleteMessage(messageId, phone, !deleteForEveryOne);
        service.sendMessage(listeners.get(phone), messagePayload);
    }
}
