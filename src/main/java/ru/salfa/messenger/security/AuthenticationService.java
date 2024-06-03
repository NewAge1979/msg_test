package ru.salfa.messenger.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.salfa.messenger.dto.request.GetOtpCodeRequest;
import ru.salfa.messenger.dto.request.SignInRequest;
import ru.salfa.messenger.dto.request.SignUpRequest;
import ru.salfa.messenger.dto.response.TokensResponse;
import ru.salfa.messenger.entity.OtpCode;
import ru.salfa.messenger.entity.User;
import ru.salfa.messenger.exception.UserAlreadyExistsException;
import ru.salfa.messenger.exception.UserNotFoundException;
import ru.salfa.messenger.repository.OtpCodeRepository;
import ru.salfa.messenger.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static java.time.OffsetDateTime.now;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;

    private final UserDetailServiceImpl userDetailService;
    private final AuthenticationManager authenticationManager;

    public void getOtpCode(GetOtpCodeRequest request) {
        log.debug("Phone: {}, action: {}", request.phone(), request.action());
        Optional<User> user = checkUser(request);
        if (user.isPresent()) {
            String otpCodeValue = RandomStringUtils.randomNumeric(6);
            log.debug("OTP code: {}", otpCodeValue);
            Optional<List<OtpCode>> otpCodes = otpCodeRepository.findByUserId(user.get().getId());
            otpCodes.ifPresent(c -> c.forEach(x -> {
                x.setIsExpired(true);
                otpCodeRepository.save(x);
            }));
            OtpCode otpCode = OtpCode
                    .builder()
                    .otpCode(otpCodeValue)
                    .user(user.get())
                    .created(now())
                    .expires(now().plusMinutes(5L))
                    .isExpired(false)
                    .build();
            otpCodeRepository.save(otpCode);
            //TODO: Add send OTP code
        } else {
            throw new UserNotFoundException("User not found.");
        }
    }

    public TokensResponse signUp(SignUpRequest request) {
        log.debug("{}", request.toString());
        UserDetails user = userDetailService.loadUserByUsername(request.phone());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.phone(), request.otp()));
        return new TokensResponse("111", "222");
    }

    public TokensResponse signIn(SignInRequest request) {
        log.debug("{}", request.toString());
        return new TokensResponse("111", "222");
    }

    public TokensResponse refreshTokens() {
        return new TokensResponse("111", "222");
    }

    public void signOut() {

    }

    private Optional<User> checkUser(GetOtpCodeRequest request) throws UserAlreadyExistsException {
        if (request.action().equals("signUp")) {
            if (userRepository.existsByPhone(request.phone())) {
                throw new UserAlreadyExistsException("User already exists.");
            }
            User newUser = User
                    .builder()
                    .phone(request.phone())
                    .firstName("")
                    .nickname(request.phone())
                    .created(now())
                    .isDeleted(false)
                    .phoneIsVerified(false)
                    .emailIsConfirmed(false)
                    .build();
            log.debug("{}", newUser.toString());
            userRepository.save(newUser);
        }
        return userRepository.findByPhone(request.phone());
    }
}