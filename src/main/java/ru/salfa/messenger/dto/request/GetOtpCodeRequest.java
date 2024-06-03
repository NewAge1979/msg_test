package ru.salfa.messenger.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GetOtpCodeRequest(
        @NotNull @Size(min = 10) String phone,
        @NotNull String action
) {
}