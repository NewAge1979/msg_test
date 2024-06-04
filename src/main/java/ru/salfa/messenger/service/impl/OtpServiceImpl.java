package ru.salfa.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import ru.salfa.messenger.config.OtpConfig;
import ru.salfa.messenger.entity.LogAccess;
import ru.salfa.messenger.entity.OtpCode;
import ru.salfa.messenger.entity.User;
import ru.salfa.messenger.repository.LogAccessRepository;
import ru.salfa.messenger.repository.OtpCodeRepository;
import ru.salfa.messenger.repository.UserAndOtpCodeRepository;
import ru.salfa.messenger.service.OtpService;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.time.OffsetDateTime.now;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {
    private final OtpCodeRepository otpCodeRepository;
    private final UserAndOtpCodeRepository userAndOtpCodeRepository;
    private final LogAccessRepository logAccessRepository;
    private final OtpConfig otpConfig;

    @Override
    public void sendOTPCode(User user) {
        log.debug("sendOTPCode: {}", user.toString());
        String otpCode = generateOTPCode(user);
        //ToDo: Send SMS with OTP code.
        log.debug("Send OTP code: {} on phone: {}", otpCode, user.getPhone());
    }

    @Override
    public boolean checkOTPCode(User user, String otpCode) {
        boolean flag = userAndOtpCodeRepository.existsByPhoneAndOtpCode(user.getPhone(), otpCode);
        if (!flag) {
            logAccessRepository.save(
                    LogAccess.builder()
                            .user(user)
                            .logType(1)
                            .created(now())
                            .expired(now().plus(otpConfig.getExpiration(), ChronoUnit.MILLIS))
                            .build()
            );
        }
        return flag;
    }

    @Override
    public void clearOTPCode(User user, String otpCode) {
        Optional<OtpCode> otpCodeStr = otpCodeRepository.findByUserAndOtpCode(user, otpCode);
        otpCodeStr.ifPresent(
                t -> {
                    otpCodeStr.get().setIsExpired(true);
                    otpCodeRepository.save(otpCodeStr.get());
                }
        );
    }

    @Override
    public int countOTPCodeError(long userId) {
        return logAccessRepository.countErrorByUser(userId, now().minus(otpConfig.getIntervalForBlocking(), ChronoUnit.MILLIS), now());
    }

    @Override
    public boolean userIsBlockedByOTP(long userId) {
        log.debug("Error count: {}", countOTPCodeError(userId));
        return countOTPCodeError(userId) >= otpConfig.getNumberOfAttempts();
    }

    private String generateOTPCode(User user) {
        String otpCode = RandomStringUtils.randomNumeric(6);
        log.debug("OTP code: {}", otpCode);
        otpCodeRepository.save(
                OtpCode.builder()
                        .otpCode(otpCode)
                        .user(user)
                        .created(now())
                        .expires(now().plus(otpConfig.getExpiration(), ChronoUnit.MILLIS))
                        .isExpired(false)
                        .build()
        );
        return otpCode;
    }
}