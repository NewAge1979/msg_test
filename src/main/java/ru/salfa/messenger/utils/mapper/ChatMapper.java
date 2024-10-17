package ru.salfa.messenger.utils.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.salfa.messenger.dto.model.ChatsDto;
import ru.salfa.messenger.entity.postgres.Chat;
import ru.salfa.messenger.entity.postgres.User;
import ru.salfa.messenger.utils.mapper.helper.UserMapperHelper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, uses = UserMapperHelper.class)
public interface ChatMapper {
    @Mapping(source = "entity.created", target = "created", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(target = "participantId", expression = "java(mapUsersToIds(entity, currentUserId))")
    ChatsDto toChatDto(Chat entity, Long currentUserId);

    default List<Long> mapUsersToIds(Chat chat, Long currentUserId) {
        return chat.getParticipants().stream()
                .map(User::getId).filter(id -> !id.equals(currentUserId))
                .collect(Collectors.toList());
    }

}
