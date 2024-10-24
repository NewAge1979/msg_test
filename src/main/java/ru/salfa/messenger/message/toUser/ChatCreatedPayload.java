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
@JsonTypeName(ChatCreatedPayload.TYPE)
@ToString
public class ChatCreatedPayload extends MessageToUser {
    public static final String TYPE = "chat_created";

    @JsonProperty("chat_id")
    private Long chatId;

    private String message = "You have been added to a new chat!";
    @JsonProperty("sender")
    private Long sender;

    @Override
    public String getType() {
        return TYPE;
    }
}
