package ru.salfa.messenger.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class PhoneOtpAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String otpCode;

    public PhoneOtpAuthenticationToken(Object principal, Object credentials, String otpCode, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.otpCode = otpCode;
    }

    public PhoneOtpAuthenticationToken(Object principal, Object credentials, String otpCode) {
        super(principal, credentials);
        this.otpCode = otpCode;
    }
}