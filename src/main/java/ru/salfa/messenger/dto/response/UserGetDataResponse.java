package ru.salfa.messenger.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserGetDataResponse(
        @NotNull @Size(max = 16) String phone,
        @Size(max = 64) String lastName,
        @NotNull @Size(max = 64) String firstName,
        @NotNull @Size(max = 32) String nickName,
        String aboutMe
) {}