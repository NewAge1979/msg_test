package ru.salfa.messenger.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateDataRequest (
        @Size(max = 64) String lastName,
        @NotNull @Size(max = 64) String firstName,
        @NotNull @Size(max = 32) String nickName,
        String aboutMe
) {}