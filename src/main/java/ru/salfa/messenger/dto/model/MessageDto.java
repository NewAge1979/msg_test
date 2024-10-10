package ru.salfa.messenger.dto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.salfa.messenger.entity.enums.MessageType;

import java.lang.management.MemoryType;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MessageDto {
    private Long id;
    private String message;
    private Long sender;
    private String created;
    private Boolean isMyMessage;
    private Boolean isRead;
    private String type = MessageType.TEXT.toString();
    private List<Document> attachments;
}
