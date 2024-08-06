package ru.salfa.messenger.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignInRequest(
        @NotEmpty(message = "The phone number cannot be empty.")
        @NotNull(message = "The phone number cannot be null.")
        @Size(min = 10, max = 10, message = "The phone number must contain 10 digits.")
        @Pattern(regexp = "^9\\d{9}$", message = "Phone number is not correct.")
        String phone,
        @NotEmpty(message = "OTP cannot be empty.")
        @NotNull(message = "OTP cannot be null.")
        String otpCode
) {
    @Override
    public String toString() {
        return "SignInRequest [Phone: " + phone + ", OTP code: " + otpCode + "]";
    }
}