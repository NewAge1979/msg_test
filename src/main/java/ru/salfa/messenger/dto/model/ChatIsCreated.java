package ru.salfa.messenger.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.salfa.messenger.entity.postgres.Chat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatIsCreated {
    private Chat chat;
    private boolean isCreated;


}
