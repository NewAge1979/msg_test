package ru.salfa.messenger.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @ManyToMany
    private List<User> participants = new ArrayList<>();
    private LocalDateTime created;
    private LocalDateTime modified;

    public void addParticipant(User participant) {
        participants.add(participant);
    }
}
