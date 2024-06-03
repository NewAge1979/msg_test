package ru.salfa.messenger.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.salfa.messenger.dto.request.GetOtpCodeRequest;
import ru.salfa.messenger.entity.User;
import ru.salfa.messenger.exception.UserAlreadyExistsException;
import ru.salfa.messenger.exception.UserNotFoundException;
import ru.salfa.messenger.repository.OtpCodeRepository;
import ru.salfa.messenger.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpCodeRepository otpCodeRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private final String PHONE = "0000000000";
    private final String ACTION1 = "signUp";
    private final String ACTION2 = "signIn";
    User user = User.builder().phone(PHONE).firstName("").nickname(PHONE).created(OffsetDateTime.now()).build();
    GetOtpCodeRequest request1 = new GetOtpCodeRequest(PHONE, ACTION1);
    GetOtpCodeRequest request2 = new GetOtpCodeRequest(PHONE, ACTION2);

    @Test
    void getOtpCodeSuccess1() {
        given(userRepository.existsByPhone(PHONE)).willReturn(false);
        given(userRepository.findByPhone(PHONE)).willReturn(Optional.of(user));
        assertDoesNotThrow(() -> authenticationService.getOtpCode(request1));
    }

    @Test
    void getOtpCodeSuccess2() {
        given(userRepository.existsByPhone(PHONE)).willReturn(true);
        given(userRepository.findByPhone(PHONE)).willReturn(Optional.of(user));
        assertDoesNotThrow(() -> authenticationService.getOtpCode(request2));
    }

    @Test
    void getOtpCodeError1() {
        given(userRepository.existsByPhone(PHONE)).willReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> authenticationService.getOtpCode(request1));
    }

    @Test
    void getOtpCodeError2() {
        given(userRepository.findByPhone(PHONE)).willReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> authenticationService.getOtpCode(request2));
    }
}