package ru.salfa.messenger.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import ru.salfa.messenger.entity.postgres.User;
import ru.salfa.messenger.exception.UserBlockedException;
import ru.salfa.messenger.exception.UserNotFoundException;
import ru.salfa.messenger.exception.UserOtpException;
import ru.salfa.messenger.repository.UserRepository;
import ru.salfa.messenger.service.OtpService;

import java.util.ArrayList;

@RequiredArgsConstructor
@Slf4j
public class PhoneOtpAuthenticationProvider extends DaoAuthenticationProvider {
    private final UserRepository userRepository;
    private final OtpService otpService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = userRepository.findByPhoneAndIsDeleted(authentication.getName(), false).orElseThrow(
                () -> new UserNotFoundException("User not found.")
        );
        if (otpService.userIsBlockedByOTP(user.getId())) {
            throw new UserBlockedException("User is temporary blocked now.");
        }
        String otpCode = ((PhoneOtpAuthenticationToken) authentication).getOtpCode();
        log.debug("OTP code: {}", otpCode);
        if (!otpService.checkOTPCode(user, otpCode)) {
            throw new UserOtpException("OTP code is incorrect.");
        }
        otpService.clearOTPCode(user, otpCode);
        //return super.authenticate(authentication);
        return new UsernamePasswordAuthenticationToken(user.getUsername(), "", new ArrayList<>());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PhoneOtpAuthenticationToken.class);
    }
}