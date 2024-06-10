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

    private String message;

    public void setMessage(String nickname){
        message = String.format("Chat with %s created successfully",nickname );
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
