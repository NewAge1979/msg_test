package ru.salfa.messenger.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatsDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("chatName")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("created")
    private String created;
    @JsonProperty("avatar")
    private String avatar;
    @JsonProperty("onlineStatus")
    private String onlineStatus;
    @JsonProperty("unreadMessages")
    private Long unreadMessages;
    @JsonProperty("participantId")
    private List<Long> participantId;
}
