package ru.salfa.messenger.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotEmpty(message = "The phone number cannot be empty.")
        @NotNull(message = "The phone number cannot be null.")
        @Size(min = 10, max = 10, message = "The phone number must contain 10 digits.")
        @Pattern(regexp = "^9\\d{9}$", message = "Phone number is not correct.")
        String phone,
        @NotEmpty(message = "The first name cannot be empty.")
        @NotNull(message = "The first name cannot be null.")
        @Size(min = 2, max = 64, message = "The first name must contain from 2 to 64 characters.")
        @Pattern(regexp = "^[A-Za-z0-9А-Яа-яЁё\\- ]{2,64}$", message = "First name is not correct.")
        String firstName,
        @Size(min = 2, max = 64, message = "The last name must contain from 2 to 64 characters.")
        @Pattern(regexp = "^[A-Za-z0-9А-Яа-яЁё\\- ]{0,64}$", message = "Last name is not correct.")
        String lastName,
        @NotEmpty(message = "The nickname cannot be empty.")
        @NotNull(message = "The nickname cannot be null.")
        @Size(min = 5, max = 32, message = "The nickname must contain from 5 to 32 characters.")
        @Pattern(regexp = "^[A-Za-z0-9_\\- ]{5,32}$", message = "Nickname is not correct.")
        String nickName,
        @Size(min = 0, max = 140, message = "The about me must contain from 0 to 160 characters.")
        String aboutMe,
        @NotEmpty(message = "OTP cannot be empty.")
        @NotNull(message = "OTP cannot be null.")
        String otp
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