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
    @JsonProperty("chat_name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("created")
    private String created;
    @JsonProperty("participant_id")
    private List<Long> participantId;
    @JsonProperty("messages")
    private List<MessageDto> messages;
}
