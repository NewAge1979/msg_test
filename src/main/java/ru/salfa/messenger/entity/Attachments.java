package ru.salfa.messenger.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.net.URL;

@Entity
@Data
public class Attachments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // todo create enum type
    private String type;
    private URL url;
    @ManyToOne
    @JoinColumn(name = "message_id")
    private Messages message;
}
