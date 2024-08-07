package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.salfa.messenger.entity.postgres.OtpCode;
import ru.salfa.messenger.entity.postgres.User;

import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByUserAndOtpCode(User user, String otpCode);
}