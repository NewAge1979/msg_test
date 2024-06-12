package ru.salfa.messenger.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotNull @Size(min = 10) String phone,
        @NotNull @Size(min = 2, max = 64) String firstName,
        @Size(min = 2, max = 64) String lastName,
        @NotNull @Size(min = 0, max = 32) String nickName,
        @Size(max = 160) String aboutMe,
        @NotNull @Size(min = 6, max = 6) String otp
) {
    @Override
    public String toString() {
        return "SignInRequest [Phone: " + phone
                + ", First name: " + firstName
                + ", Last name: " + lastName
                + ", Nickname: " + nickName
                + ", About Me: " + aboutMe
                + ", OTP code: " + otp
                + "]";
    }
}