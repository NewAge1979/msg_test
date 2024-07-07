package ru.salfa.messenger.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.salfa.messenger.message.toUser.*;


@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExceptionPayload.class, name = ExceptionPayload.TYPE),
        @JsonSubTypes.Type(value = ChatClearedPayload.class, name = ChatClearedPayload.TYPE),
        @JsonSubTypes.Type(value = ChatCreatedPayload.class, name = ChatCreatedPayload.TYPE),
        @JsonSubTypes.Type(value = ChatListPayload.class, name = ChatListPayload.TYPE),
        @JsonSubTypes.Type(value = ChatMessagePayload.class, name = ChatMessagePayload.TYPE),
        @JsonSubTypes.Type(value = MessageDeletedPayload.class, name = MessageDeletedPayload.TYPE),
        @JsonSubTypes.Type(value = SearchMessagesResultsPayload.class, name = SearchMessagesResultsPayload.TYPE),
        @JsonSubTypes.Type(value = SuccessSendPayload.class, name = SuccessSendPayload.TYPE),
        @JsonSubTypes.Type(value = SearchUsersResultsPayload.class, name = SearchUsersResultsPayload.TYPE)
})
public class MessageToUser {
    @JsonIgnore
    private String type;

    @JsonIgnore
    public String getType() {
        return type;
    }
}
