package ru.salfa.messenger.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import ru.salfa.messenger.entity.User;
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
        if (authentication.getDetails() == null) {
            log.debug("getDetails is null!!!");
        }
        User user = userRepository.findByPhone(authentication.getName()).orElseThrow(
                () -> new UserNotFoundException("User not found.")
        );
        if (otpService.userIsBlockedByOTP(user.getId())) {
            throw new UserBlockedException("User is temporary blocked now.");
        }
        /*String otpCode = ((PhoneAuthenticationDetails) authentication.getDetails()).getOtpCode();
        if (!otpService.checkOTPCode(user, otpCode)) {
            throw new UserOtpException("OTP code is incorrect.");
        }*/
        //return super.authenticate(authentication);
        return new UsernamePasswordAuthenticationToken(user.getUsername(), "", new ArrayList<>());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}