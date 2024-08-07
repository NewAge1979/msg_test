package ru.salfa.messenger.entity.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "otp_codes")
public class OtpCode {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "otp_codes_id_gen")
    @SequenceGenerator(name = "otp_codes_id_gen", sequenceName = "otp_codes_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 6)
    @NotNull
    @Column(name = "otp_code", nullable = false, length = 6)
    private String otpCode;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "created", nullable = false)
    private OffsetDateTime created;

    @Column(name = "expires")
    private OffsetDateTime expires;

    @Column(name = "is_expired")
    private Boolean isExpired;
}