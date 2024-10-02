package ru.salfa.messenger.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.salfa.messenger.dto.request.GetOtpCodeRequest;
import ru.salfa.messenger.dto.request.SignInRequest;
import ru.salfa.messenger.dto.response.SignInResponse;
import ru.salfa.messenger.security.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/getOTPCode")
    ResponseEntity<Void> getOtpCode(@Valid @RequestBody GetOtpCodeRequest request) {
        log.debug("Endpoint: /api/v1/auth/getOTPCode. Method: POST.");
        authenticationService.getOtpCode(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signIn")
    ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest request,
                                          HttpServletResponse response,
                                          @Value("${jwt.refresh-token-expiration}") String refreshTokenExpiration) {
        log.debug("Endpoint: /api/v1/auth/signIn. Method: POST.");
        var signInResponse = authenticationService.signIn(request);

        addRefreshCookie(signInResponse.refreshToken(), refreshTokenExpiration, response);
        return ResponseEntity.ok(signInResponse);
    }

    @PostMapping("/refreshTokens")
    ResponseEntity<?> refreshTokens(HttpServletRequest request, HttpServletResponse response,
                                    @Value("${jwt.refresh-token-expiration}") String refreshTokenExpiration) {
        log.debug("Endpoint: /api/v1/auth/refreshTokens. Method: POST.");

        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken == null || !authenticationService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        var tokens = authenticationService.refreshTokens(refreshToken);

        addRefreshCookie(tokens.refreshToken(), refreshTokenExpiration, response);

        return ResponseEntity.ok(tokens);
    }

    private static void addRefreshCookie(String tokens, String refreshTokenExpiration, HttpServletResponse response) {
        response.addHeader("Set-Cookie", String.format(
                "refreshToken=%s; Max-Age=%d; Path=/api/v1/auth/refreshTokens; HttpOnly; Secure; SameSite=None",
                tokens, (int) (Long.parseLong(refreshTokenExpiration) / 1000)
        ));


    }

    @PostMapping("/signOut")
    ResponseEntity<Void> signOut(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Endpoint: /api/v1/auth/signOut. Method: POST.");
        authenticationService.signOut(request, response);
        return ResponseEntity.ok().build();
    }
}