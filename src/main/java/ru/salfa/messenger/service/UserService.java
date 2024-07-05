package ru.salfa.messenger.service;

import ru.salfa.messenger.dto.request.UserUpdateDataRequest;
import ru.salfa.messenger.dto.response.UserGetDataResponse;

public interface UserService {
    UserGetDataResponse getUserData(String phone);

    UserGetDataResponse updateUserData(String phone, UserUpdateDataRequest userUpdateDataRequest);
}