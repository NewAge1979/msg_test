package ru.salfa.messenger.message.toUser;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.salfa.messenger.dto.model.UserDto;
import ru.salfa.messenger.message.MessageToUser;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName(SearchUsersResultsPayload.TYPE)
@ToString
public class SearchUsersResultsPayload extends MessageToUser {
    public static final String TYPE = "search_user_results";
    private List<UserDto> users;

    @Override
    public String getType() {
        return TYPE;
    }
}
