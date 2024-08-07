package ru.salfa.messenger.dto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private List<Document> attachments;
}
