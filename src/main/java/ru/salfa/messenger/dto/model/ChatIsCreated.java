package ru.salfa.messenger.dto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.salfa.messenger.entity.Chat;

@Getter
@Setter
@NoArgsConstructor
public class ChatIsCreated {
    private Chat chat;
    private boolean isCreated;
}
