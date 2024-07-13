package ru.salfa.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import ru.salfa.messenger.component.SmsSender;
import ru.salfa.messenger.config.OtpConfig;
import ru.salfa.messenger.entity.LogAccess;
import ru.salfa.messenger.entity.OtpCode;
import ru.salfa.messenger.entity.User;
import ru.salfa.messenger.repository.BlockedUserRepository;
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
    private final SmsSender smsSender;
    private final BlockedUserRepository blockedUserRepository;

    @Override
    public void sendOTPCode(User user) {
        log.debug("sendOTPCode: {}", user.toString());
        String otpCode = generateOTPCode(user);

        smsSender.sendSms(user.getPhone(), otpCode);
        log.debug("Send OTP code: {} on phone: {}", otpCode, user.getPhone());
    }

    @Override
    public boolean checkOTPCode(User user, String otpCode) {
        boolean flag = userAndOtpCodeRepository.existsByPhoneAndOtpCode(user.getPhone(), otpCode);
        if (!flag) {
            addLogAccessEvent(user, 1);
        }
        if (countOTPCodeError(user.getId()) >= otpConfig.getNumberOfAttempts()) {
            addLogAccessEvent(user, 2);
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
        return blockedUserRepository.existsByUserId(userId);
    }

    private String generateOTPCode(User user) {
        String otpCode = RandomStringUtils.randomNumeric(otpConfig.getLength());
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

    private void addLogAccessEvent(User user, Integer eventId) {
        logAccessRepository.save(
                LogAccess.builder()
                        .user(user)
                        .logType(eventId)
                        .created(now())
                        .expired(now().plus(
                                eventId == 1 ? 0 : otpConfig.getDurationOfBlocking(),
                                ChronoUnit.MILLIS
                        ))
                        .build()
        );
    }
}
