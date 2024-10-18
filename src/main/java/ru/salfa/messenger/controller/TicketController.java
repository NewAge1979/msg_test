package ru.salfa.messenger.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.salfa.messenger.dto.request.TicketRequest;
import ru.salfa.messenger.service.TicketService;

// /api/v1/support/createTicket
@RestController
@RequestMapping("/api/v1/support")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping("/createTicket")
    ResponseEntity<Void> createTicket(@Valid @RequestBody TicketRequest ticketRequest) {
        ticketService.createTicket(ticketRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}