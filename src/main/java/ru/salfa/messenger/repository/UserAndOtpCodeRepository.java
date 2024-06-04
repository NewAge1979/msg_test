package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.salfa.messenger.entity.UserAndOtpCode;

public interface UserAndOtpCodeRepository extends JpaRepository<UserAndOtpCode, Long> {
    boolean existsByPhoneAndOtpCode(String phone, String otpCode);
}