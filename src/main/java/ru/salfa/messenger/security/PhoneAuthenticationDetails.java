package ru.salfa.messenger.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.io.IOException;

@Getter
@Slf4j
public class PhoneAuthenticationDetails extends WebAuthenticationDetails {
    private final String otpCode;

    public PhoneAuthenticationDetails(HttpServletRequest request) {
        super(request);
        log.debug("PhoneAuthenticationDetails");
        otpCode = request.getParameter("otpCode");
    }
}