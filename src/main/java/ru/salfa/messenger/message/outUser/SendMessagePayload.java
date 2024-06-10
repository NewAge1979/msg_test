package ru.salfa.messenger.message.outUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.AttachmentsDto;
import ru.salfa.messenger.entity.User;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.ChatMessagePayload;
import ru.salfa.messenger.service.ChatService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@JsonTypeName(SendMessagePayload.ACTION)
@ToString
public class SendMessagePayload extends MessageOutUser {
    public static final String ACTION = "send_message";

    private String message;
    @JsonProperty("participant_id")
    private Long participantId;
    private List<AttachmentsDto> attachments;

    @Override
    public String getAction() {
        return ACTION;
    }

    @SneakyThrows
    @Override
    public void handler(ChatService service, Map<Long, WebSocketSession> listeners, Long userId) {
        var chat = service.getChat(participantId, userId).getChat();

        var messagePayload = new ChatMessagePayload();

        var msgDto = service.createAndSaveMsg(chat.getId(), userId, message, attachments);
        messagePayload.setMessage(msgDto);
        messagePayload.setChatId(chat.getId());

        var participants = chat.getParticipants().stream().map(User::getId)
                .filter(id -> !id.equals(userId)).collect(Collectors.toCollection(ArrayList::new));
        for (var id : participants) {
            if (listeners.containsKey(id)) {
                service.sendMessage(listeners.get(id), messagePayload);
            }
        }
    }
}
