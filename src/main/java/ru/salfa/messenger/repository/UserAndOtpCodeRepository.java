package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.salfa.messenger.entity.UserAndOtpCode;

import java.util.Optional;

public interface UserAndOtpCodeRepository extends JpaRepository<UserAndOtpCode, Long> {
    Optional<UserAndOtpCode> findByPhoneAndOtpCode(String phone, String otpCode);
}