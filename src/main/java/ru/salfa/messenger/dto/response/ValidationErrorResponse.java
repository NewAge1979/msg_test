package ru.salfa.messenger.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.salfa.messenger.dto.model.Violation;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse {
    private final List<Violation> violations;
}