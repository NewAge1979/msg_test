package ru.salfa.messenger.message.toUser;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.MessageToUser;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName(ExceptionPayload.TYPE)
@ToString
public class ExceptionPayload extends MessageToUser {
    public static final String TYPE = "exception";
    private String request;
    private String exception;

    @Override
    public String getType() {
        return TYPE;
    }
}
