package ru.salfa.messenger.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.salfa.messenger.dto.request.GetOtpCodeRequest;
import ru.salfa.messenger.dto.request.SignInRequest;
import ru.salfa.messenger.dto.response.SignInResponse;
import ru.salfa.messenger.dto.response.TokensResponse;
import ru.salfa.messenger.entity.User;
import ru.salfa.messenger.exception.UserBlockedException;
import ru.salfa.messenger.exception.UserNotFoundException;
import ru.salfa.messenger.exception.UserOtpException;
import ru.salfa.messenger.repository.NewUserRepository;
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
    private final UserDetailServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final NewUserRepository newUserRepository;

    public void getOtpCode(GetOtpCodeRequest request) {
        log.debug("Phone: {}", request.phone());
        if (!userRepository.existsByPhone(request.phone())) {
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
        User user = userRepository.findByPhone(request.phone()).orElseThrow(
                () -> new UserNotFoundException("User not found.")
        );
        otpService.sendOTPCode(user);
    }

    public SignInResponse signIn(SignInRequest request) {
        log.debug("{}", request.toString());
        // Update user data, create and send tokens.
        User user = userRepository.findByPhone(request.phone()).orElseThrow(
                () -> new UserNotFoundException("User not found.")
        );
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getPhone());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            var jwtAccessToken = jwtService.createAccessToken(userDetails);
            var jwtRefreshToken = jwtService.createRefreshToken(userDetails);
            boolean isNewUser = newUserRepository.existsById(user.getId());

            log.debug("jwtAccessToken: {}", jwtAccessToken);
            log.debug("jwtRefreshToken: {}", jwtRefreshToken);
            log.debug("isNewUser: {}", isNewUser);

            return new SignInResponse(jwtAccessToken, jwtRefreshToken, isNewUser);
        } catch (UsernameNotFoundException e) {
            throw new UserNotFoundException("User not found.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
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