package ru.salfa.messenger.utils.mapper.helper;


import org.mapstruct.Named;
import ru.salfa.messenger.entity.postgres.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapperHelper {
    @Named("mapUsersToIds")
    public static List<Long> mapUsersToIds(List<User> users) {
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
