package ru.salfa.messenger.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPhoneRequest {
    @NotEmpty(message = "The phone number cannot be empty.")
    @NotNull(message = "The phone number cannot be null.")
    @Size(min = 10, max = 10, message = "The phone number must contain 10 digits.")
    @Pattern(regexp = "^9\\d{9}$", message = "Phone number is not correct.")
    String phone;
}