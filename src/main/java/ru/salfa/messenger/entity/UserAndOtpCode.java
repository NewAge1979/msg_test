package ru.salfa.messenger.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Getter
@Entity
@Immutable
@Table(name = "user_and_otp_code")
public class UserAndOtpCode {
    @Id
    @Column(name = "id")
    private Long id;

    @Size(max = 16)
    @Column(name = "phone", length = 16)
    private String phone;

    @Size(max = 6)
    @Column(name = "otp_code", length = 6)
    private String otpCode;
}