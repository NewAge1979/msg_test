package ru.salfa.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.salfa.messenger.dto.request.TicketRequest;
import ru.salfa.messenger.entity.postgres.Ticket;
import ru.salfa.messenger.repository.TicketRepository;
import ru.salfa.messenger.service.TicketService;

import static java.time.OffsetDateTime.now;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;

    @Override
    public Ticket createTicket(TicketRequest ticket) {
        Ticket ticketEntity = Ticket.builder()
                .phone(ticket.getPhone())
                .email(ticket.getEmail())
                .created(now())
                .ticketText(ticket.getTicketText())
                .status(0)
                .build();
        ticketRepository.save(ticketEntity);
        return ticketEntity;
    }
}