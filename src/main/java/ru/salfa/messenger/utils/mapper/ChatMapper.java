package ru.salfa.messenger.utils.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.salfa.messenger.dto.model.ChatsDto;
import ru.salfa.messenger.entity.postgres.Chat;
import ru.salfa.messenger.utils.mapper.helper.UserMapperHelper;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, uses = UserMapperHelper.class)
public interface ChatMapper {
    @Mapping(source = "participants", target = "participantId", qualifiedByName = "mapUsersToIds")
    @Mapping(source = "created", target = "created", dateFormat = "HH:mm:ss dd.MM.yyyy")
    ChatsDto toChatDto(Chat entity);
}
