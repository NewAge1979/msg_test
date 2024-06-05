package ru.salfa.messenger.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PhoneAuthenticationDetailsSource implements AuthenticationDetailsSource <HttpServletRequest, PhoneAuthenticationDetails>{
    @Override
    public PhoneAuthenticationDetails buildDetails(HttpServletRequest context) {
        log.debug("PhoneAuthenticationDetailsSource");
        return new PhoneAuthenticationDetails(context);
    }
}