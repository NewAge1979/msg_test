package ru.salfa.messenger.utils.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.salfa.messenger.dto.model.ChatsDto;
import ru.salfa.messenger.entity.Chat;
import ru.salfa.messenger.utils.mapper.helper.UserMapperHelper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, uses = UserMapperHelper.class)
public interface ChatMapper {
    @Mapping(source = "participants", target = "participantId", qualifiedByName = "mapUsersToIds")
    @Mapping(source = "created", target = "created", dateFormat = "HH:mm:ss dd.MM.yyyy")
    ChatsDto toChatDto(Chat entity);
}
