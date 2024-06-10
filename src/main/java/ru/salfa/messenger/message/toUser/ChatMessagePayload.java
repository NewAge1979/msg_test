package ru.salfa.messenger.message.toUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.salfa.messenger.dto.model.MessageDto;
import ru.salfa.messenger.message.MessageToUser;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName(ChatMessagePayload.TYPE)
@ToString
public class ChatMessagePayload extends MessageToUser {
    public static final String TYPE = "chat_message";

    @JsonProperty("chat_id")
    private Long chatId;
    private MessageDto message;
    @JsonProperty("original_sender")
    private String originalSender;

    @Override
    public String getType() {
        return TYPE;
    }
}
