package ru.salfa.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.salfa.messenger.dto.request.UserUpdateDataRequest;
import ru.salfa.messenger.dto.response.UserGetDataResponse;
import ru.salfa.messenger.entity.postgres.User;
import ru.salfa.messenger.exception.UserNotFoundException;
import ru.salfa.messenger.repository.UserRepository;
import ru.salfa.messenger.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserGetDataResponse getUserData(String phone) {
        User user = userRepository.findByPhoneAndIsDeleted(phone, false).orElseThrow(
                () -> new UserNotFoundException("User not found.")
        );

        return new UserGetDataResponse(
                user.getPhone(),
                user.getLastName(),
                user.getFirstName(),
                user.getNickname(),
                user.getPersonalInformation()
        );
    }

    @Override
    public UserGetDataResponse updateUserData(String phone, UserUpdateDataRequest userUpdateDataRequest) {
        User user = userRepository.findByPhoneAndIsDeleted(phone, false).orElseThrow(
                () -> new UserNotFoundException("User not found.")
        );

        user.setLastName(userUpdateDataRequest.lastName());
        user.setFirstName(userUpdateDataRequest.firstName());
        user.setNickname(userUpdateDataRequest.nickName());
        user.setPersonalInformation(userUpdateDataRequest.aboutMe());

        userRepository.save(user);

        return new UserGetDataResponse(
                user.getPhone(),
                user.getLastName(),
                user.getFirstName(),
                user.getNickname(),
                user.getPersonalInformation()
        );
    }
}