package ru.salfa.messenger.service;

import ru.salfa.messenger.entity.postgres.User;

public interface OtpService {
    void sendOTPCode(User user);

    boolean checkOTPCode(User user, String otpCode);

    void clearOTPCode(User user, String otpCode);

    int countOTPCodeError(long userId);

    boolean userIsBlockedByOTP(long userId);
}