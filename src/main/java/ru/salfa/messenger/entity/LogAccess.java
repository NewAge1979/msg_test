package ru.salfa.messenger.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "log_access")
public class LogAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_access_id_gen")
    @SequenceGenerator(name = "log_access_id_gen", sequenceName = "log_access_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "log_type", nullable = false)
    private Integer logType;

    @NotNull
    @Column(name = "created", nullable = false)
    private OffsetDateTime created;

    @NotNull
    @Column(name = "expired", nullable = false)
    private OffsetDateTime expired;
}