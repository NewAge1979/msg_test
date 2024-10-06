package ru.salfa.messenger.message.outUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.web.socket.WebSocketSession;
import ru.salfa.messenger.dto.model.UserDto;
import ru.salfa.messenger.message.MessageOutUser;
import ru.salfa.messenger.message.toUser.SearchUsersResultsPayload;
import ru.salfa.messenger.service.ChatService;

import java.util.List;
import java.util.Map;

@Setter
@JsonTypeName(SearchUserPayload.ACTION)
public class SearchUserPayload extends MessageOutUser {
    public static final String ACTION = "search_user";


    @JsonProperty("search_data")
    private String searchData;

    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    @SneakyThrows
    public void handler(ChatService service, Map<String, WebSocketSession> listeners, String userPhone) {
        List<UserDto> userDtos;
        if(searchData.matches("^9\\d{9}$")){
            userDtos = service.getListUserDtoByPhone(searchData);
        } else {
            userDtos = service.getListUserDtoByNickname(searchData);
        }

        var payload = new SearchUsersResultsPayload();
        payload.setUsers(userDtos);

        service.sendMessage(listeners.get(userPhone), payload);
    }
}
