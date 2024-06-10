package ru.salfa.messenger.message.toUser;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.salfa.messenger.message.MessageToUser;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName(ChatClearedPayload.TYPE)
@ToString
public class ChatClearedPayload extends MessageToUser {
    public static final String TYPE = "chat_cleared";

    @JsonProperty("chat_id")
    private Long chatId;
    private List<Object> message = List.of();

    @Override
    public String getType() {
        return TYPE;
    }
}