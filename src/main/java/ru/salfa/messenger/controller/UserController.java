package ru.salfa.messenger.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.salfa.messenger.dto.request.UserPhoneRequest;
import ru.salfa.messenger.dto.request.UserUpdateDataRequest;
import ru.salfa.messenger.dto.response.UserGetDataResponse;
import ru.salfa.messenger.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/getData")
    ResponseEntity<UserGetDataResponse> getUserData(@Valid @RequestParam UserPhoneRequest phone) {
        log.debug("Endpoint: /api/v1/user/getData. Method: GET.");
        return ResponseEntity.ok(userService.getUserData(phone.getPhone()));
    }

    @PutMapping("/updateData")
    ResponseEntity<UserGetDataResponse> updateUserData(
            @Valid @RequestParam UserPhoneRequest phone,
            @Valid @RequestBody UserUpdateDataRequest userUpdateDataRequest
    ) {
        log.debug("Endpoint: /api/v1/user/updateData. Method: PUT.");
        return ResponseEntity.ok(userService.updateUserData(phone.getPhone(), userUpdateDataRequest));
    }
}