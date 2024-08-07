package ru.salfa.messenger.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import ru.salfa.messenger.dto.request.GetOtpCodeRequest;
import ru.salfa.messenger.dto.request.SignInRequest;
import ru.salfa.messenger.dto.response.SignInResponse;
import ru.salfa.messenger.dto.response.TokensResponse;
import ru.salfa.messenger.entity.postgres.User;
import ru.salfa.messenger.exception.UserNotFoundException;
import ru.salfa.messenger.repository.NewUserRepository;
import ru.salfa.messenger.repository.UserRepository;
import ru.salfa.messenger.service.OtpService;

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
        if (!userRepository.existsByPhone(request.getPhone())) {
            userRepository.save(
                    User.builder()
                            .phone(request.getPhone())
                            .firstName("")
                            .nickname("NewUser#" + request.getPhone())
                            .created(now())
                            .isDeleted(false)
                            .phoneIsVerified(false)
                            .emailIsConfirmed(false)
                            .build()
            );
        }
        User user = userRepository.findByPhoneAndIsDeleted(request.getPhone(), false).orElseThrow(
                () -> new UserNotFoundException("User not found.")
        );
        otpService.sendOTPCode(user);
    }

    public SignInResponse signIn(SignInRequest request) {
        // Update user data, create and send tokens.
        User user = userRepository.findByPhoneAndIsDeleted(request.phone(), false).orElseThrow(
                () -> new UserNotFoundException("User not found.")
        );
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getPhone());
            authenticationManager.authenticate(new PhoneOtpAuthenticationToken(user.getUsername(), user.getPassword(), request.otpCode()));
            var jwtAccessToken = jwtService.createAccessToken(userDetails);
            var jwtRefreshToken = jwtService.createRefreshToken(userDetails);
            boolean isNewUser = newUserRepository.existsById(user.getId());
            return new SignInResponse(jwtAccessToken, jwtRefreshToken, isNewUser);
        } catch (UsernameNotFoundException e) {
            throw new UserNotFoundException("User not found.");
        }
    }

    public TokensResponse refreshTokens() {
        UserDetails userDetails = userDetailsService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        var jwtAccessToken = jwtService.createAccessToken(userDetails);
        var jwtRefreshToken = jwtService.createRefreshToken(userDetails);
        return new TokensResponse(jwtAccessToken, jwtRefreshToken);
    }

    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            jwtService.removeTokens(auth.getName());
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }
}