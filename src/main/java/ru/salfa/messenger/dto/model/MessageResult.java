package ru.salfa.messenger.dto.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class MessageResult {
    private String date;
    private List<MessageDto> messagesInDate;

    public MessageResult(String date, List<MessageDto> messages) {
        this.date = date;
        this.messagesInDate = messages;
    }

    public static List<MessageResult> ofListMessageDto(Map<String, List<MessageDto>> messages) {
        List<MessageResult> results = new ArrayList<>();
        for (var data : messages.keySet()) {
            results.add(new MessageResult(data, messages.get(data)));
        }
        return results;
    }
}

