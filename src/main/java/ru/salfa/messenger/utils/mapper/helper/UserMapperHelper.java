package ru.salfa.messenger.utils.mapper.helper;


import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.salfa.messenger.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapperHelper {
    @Named("mapUsersToIds")
    public static List<Long> mapUsersToIds(List<User> users) {
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
