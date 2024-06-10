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
@JsonTypeName(SuccessForwardedPayload.TYPE)
@ToString
public class SuccessForwardedPayload extends MessageToUser {
    public static final String TYPE = "confirmation";

    @JsonProperty("chat_id")
    private Long chatId;
    private String message = "Message forwarded successfully";
    private boolean created;

    @Override
    public String getType() {
        return TYPE;
    }
}

