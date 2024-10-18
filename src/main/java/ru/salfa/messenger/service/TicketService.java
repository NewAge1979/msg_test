package ru.salfa.messenger.service;

import ru.salfa.messenger.dto.request.TicketRequest;
import ru.salfa.messenger.entity.postgres.Ticket;

public interface TicketService {
    Ticket createTicket(TicketRequest ticket);
}