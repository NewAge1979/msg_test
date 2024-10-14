package ru.salfa.messenger.entity.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 16)
    @NotNull
    @Column(name = "phone", nullable = false, length = 16)
    private String phone;

    @Size(max = 64)
    @NotNull
    @Column(name = "email", length = 64)
    private String email;

    @NotNull
    @Column(name = "created", nullable = false)
    private OffsetDateTime created;

    @Column(name = "modified")
    private OffsetDateTime modified;

    @Size(min = 3, max = 500)
    @NotNull
    @Column(name = "ticket_text", length = Integer.MAX_VALUE)
    private String ticketText;

    @Column(name = "status")
    private Integer status;

}