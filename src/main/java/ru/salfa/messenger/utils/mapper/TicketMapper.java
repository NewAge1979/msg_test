package ru.salfa.messenger.utils.mapper;

import org.mapstruct.*;
import ru.salfa.messenger.dto.request.TicketRequest;
import ru.salfa.messenger.entity.postgres.Ticket;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TicketMapper {
    Ticket toEntity(TicketRequest ticketRequest);

    TicketRequest toDto(Ticket ticket);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Ticket partialUpdate(TicketRequest ticketRequest, @MappingTarget Ticket ticket);
}