package ru.salfa.messenger.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.salfa.messenger.dto.request.GetOtpCodeRequest;
import ru.salfa.messenger.dto.request.SignInRequest;
import ru.salfa.messenger.dto.request.SignUpRequest;
import ru.salfa.messenger.dto.response.TokensResponse;
import ru.salfa.messenger.entity.User;
import ru.salfa.messenger.exception.UserAlreadyExistsException;
import ru.salfa.messenger.exception.UserBlockedException;
import ru.salfa.messenger.exception.UserNotFoundException;
import ru.salfa.messenger.exception.UserOtpException;
import ru.salfa.messenger.repository.UserRepository;
import ru.salfa.messenger.service.OtpService;

import java.util.Optional;

import static java.time.OffsetDateTime.now;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final OtpService otpService;

    public void getOtpCode(GetOtpCodeRequest request) {
        log.debug("Phone: {}, action: {}", request.phone(), request.action());
        boolean phoneExists = userRepository.existsByPhone(request.phone());
        if (request.action().equals("signUp")) {
            if (phoneExists) {
                throw new UserAlreadyExistsException("User already exists.");
            } else {
                userRepository.save(
                        User.builder()
                                .phone(request.phone())
                                .firstName("")
                                .nickname("NewUser#" + request.phone())
                                .created(now())
                                .isDeleted(false)
                                .phoneIsVerified(false)
                                .emailIsConfirmed(false)
                                .build()
                );
            }
        } else {
            if (!phoneExists) {
                throw new UserNotFoundException("User not found.");
            }
        }
        otpService.sendOTPCode(userRepository.findByPhone(request.phone()).get());
    }

    public TokensResponse signUp(SignUpRequest request) {
        log.debug("{}", request.toString());
        if (checkUserOtpAndBlock(request.phone(), request.otp())) {
            return new TokensResponse("111", "222");
        }
        return null;
    }

    public TokensResponse signIn(SignInRequest request) {
        log.debug("{}", request.toString());
        if (checkUserOtpAndBlock(request.phone(), request.otp())) {
            return new TokensResponse("111", "222");
        }
        return null;
    }

    public TokensResponse refreshTokens() {
        return new TokensResponse("111", "222");
    }

    public void signOut() {

    }

    private boolean checkUserOtpAndBlock(
            String phone, String otpCode
    ) throws UserNotFoundException, UserBlockedException, UserOtpException {
        Optional<User> user = userRepository.findByPhone(phone);
        boolean result = false;
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found.");
        }
        User curUser = user.get();
        if (otpService.userIsBlockedByOTP(curUser.getId())) {
            throw new UserBlockedException("User is temporary blocked now.");
        }
        if (otpService.checkOTPCode(curUser, otpCode)) {
            result = true;
        } else {
            throw new UserOtpException("OTP code is incorrect.");
        }
        return result;
    }
}