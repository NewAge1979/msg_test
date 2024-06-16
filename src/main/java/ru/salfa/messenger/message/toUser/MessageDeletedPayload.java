package ru.salfa.messenger.message.toUser;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.salfa.messenger.message.MessageToUser;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName(MessageDeletedPayload.TYPE)
@ToString
public class MessageDeletedPayload extends MessageToUser {
    public static final String TYPE = "message_deleted";

    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("message_id")
    private Long messageId;

    @Override
    public String getType() {
        return TYPE;
    }
}


