package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.salfa.messenger.entity.UserAndOtpCode;

public interface UserAndOtpCodeRepository extends JpaRepository<UserAndOtpCode, Long> {
    boolean existsByPhoneAndOtpCode(@Param("phone") String phone, @Param("otpCode") String otpCode);
}