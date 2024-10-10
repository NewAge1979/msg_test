package ru.salfa.messenger.dto.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MessageResult {
    private String date;
    private MessageDto messages;

    public MessageResult(String date, MessageDto messages) {
        this.date = date;
        this.messages = messages;
    }

    public static List<MessageResult> ofListMessageDto(List<MessageDto> messageDtos) {
        List<MessageResult> results = new ArrayList<>();
        for (MessageDto messageDto : messageDtos) {
            results.add(new MessageResult(LocalDateTime.parse(messageDto.getCreated()).format(DateTimeFormatter.ISO_DATE), messageDto));
        }
        return results;
    }
}

