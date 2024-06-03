package ru.salfa.messenger.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.salfa.messenger.dto.request.GetOtpCodeRequest;
import ru.salfa.messenger.dto.request.SignInRequest;
import ru.salfa.messenger.dto.request.SignUpRequest;
import ru.salfa.messenger.dto.response.TokensResponse;
import ru.salfa.messenger.security.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/getOTPcode")
    ResponseEntity<Void> getOtpCode(@RequestBody GetOtpCodeRequest request) {
        log.debug("Endpoint: /api/v1/auth/getOTPcode. Method: POST.");
        authenticationService.getOtpCode(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signUp")
    ResponseEntity<TokensResponse> signUp(@RequestBody SignUpRequest request) {
        log.debug("Endpoint: /api/v1/auth/signUp. Method: POST.");
        return ResponseEntity.ok(authenticationService.signUp(request));
    }

    @PostMapping("/signIn")
    ResponseEntity<TokensResponse> signIn(@RequestBody SignInRequest request) {
        log.debug("Endpoint: /api/v1/auth/signIn. Method: POST.");
        return ResponseEntity.ok(authenticationService.signIn(request));
    }

    @PostMapping("/refreshTokens")
    ResponseEntity<TokensResponse> refreshTokens() {
        log.debug("Endpoint: /api/v1/auth/refreshTokens. Method: POST.");
        return ResponseEntity.ok(authenticationService.refreshTokens());
    }

    @PostMapping("/signOut")
    ResponseEntity<Void> signOut() {
        log.debug("Endpoint: /api/v1/auth/signOut. Method: POST.");
        authenticationService.signOut();
        return ResponseEntity.ok().build();
    }
}