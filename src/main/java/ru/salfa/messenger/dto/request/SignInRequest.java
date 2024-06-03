package ru.salfa.messenger.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignInRequest(
        @NotNull @Size(min = 10) String phone,
        @NotNull @Size(min = 6, max = 6) String otp
) {
    @Override
    public String toString() {
        return "SignInRequest [Phone: " + phone + ", OTP code: " + otp + "]";
    }
}