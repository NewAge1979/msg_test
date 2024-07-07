package ru.salfa.messenger.message.toUser;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.salfa.messenger.dto.model.MessageDto;
import ru.salfa.messenger.message.MessageToUser;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName(SearchMessagesResultsPayload.TYPE)
@ToString
public class SearchMessagesResultsPayload extends MessageToUser {
    public static final String TYPE = "search_results";
    private List<MessageDto> messages;

    @Override
    public String getType() {
        return TYPE;
    }
}
