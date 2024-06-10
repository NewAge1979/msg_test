package ru.salfa.messenger.utils.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.salfa.messenger.dto.model.MessageDto;
import ru.salfa.messenger.entity.Messages;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface MessageMapper {
    @Mapping(source = "senderId.id", target = "sender")
    @Mapping(source = "created", target = "created", dateFormat = "HH:mm:ss dd.MM.yyyy")
    MessageDto toMessageDto(Messages entity);
}
