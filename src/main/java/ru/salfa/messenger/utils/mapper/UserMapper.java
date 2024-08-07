package ru.salfa.messenger.utils.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.salfa.messenger.dto.model.UserDto;
import ru.salfa.messenger.entity.postgres.User;

import java.util.List;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface UserMapper {
    UserDto toUserDto(User user);
    List<UserDto> toUserDtos(List<User> users);
}
