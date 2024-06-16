package ru.salfa.messenger.message.toUser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.salfa.messenger.dto.model.ChatsDto;
import ru.salfa.messenger.message.MessageToUser;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName(ChatListPayload.TYPE)
@ToString
public class ChatListPayload extends MessageToUser {
    @JsonIgnore
    public static final String TYPE = "chat_list";
    private List<ChatsDto> chats;
}
