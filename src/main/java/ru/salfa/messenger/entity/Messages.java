package ru.salfa.messenger.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chatId;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User senderId;
    @ManyToMany
    private List<User> userDeleteMessage = new ArrayList<>();
    private boolean isDelete = false;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modified;
    @OneToMany(mappedBy = "message")
    private List<Attachments> attachments = new ArrayList<>();

    public void addUserDeleters(User user) {
        userDeleteMessage.add(user);
    }

    public void addAttachments(Attachments att) {
        attachments.add(att);
        att.setMessage(this);
    }
}
