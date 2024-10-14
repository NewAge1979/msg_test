package ru.salfa.messenger.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

/**
 * DTO for {@link ru.salfa.messenger.entity.postgres.Ticket}
 */
@Value
public class TicketRequest {
    @NotEmpty(message = "The phone number cannot be empty.")
    @NotNull(message = "The phone number cannot be null.")
    @Size(min = 10, max = 10, message = "The phone number must contain 10 digits.")
    @Pattern(regexp = "^9\\d{9}$", message = "Phone number is not correct.")
    String phone;
    @NotEmpty(message = "The e-mail cannot be empty.")
    @NotNull(message = "The e-mail cannot be null.")
    @Size(max = 64, message = "The e-mail must be no more than 64 characters long.")
    String email;
    @NotEmpty(message = "The ticket text cannot be empty.")
    @NotNull(message = "The ticket text cannot be null.")
    @Size(max = 64, message = "The ticket text must be between 3 and 500 characters long.")
    String ticketText;
}