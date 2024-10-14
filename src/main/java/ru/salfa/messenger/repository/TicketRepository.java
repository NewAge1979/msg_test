package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.salfa.messenger.entity.postgres.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}