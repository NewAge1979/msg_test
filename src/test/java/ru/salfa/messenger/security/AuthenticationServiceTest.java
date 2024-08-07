package ru.salfa.messenger.security;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.salfa.messenger.dto.request.GetOtpCodeRequest;
import ru.salfa.messenger.entity.postgres.User;
import ru.salfa.messenger.repository.OtpCodeRepository;
import ru.salfa.messenger.repository.UserRepository;

import java.time.OffsetDateTime;

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
    User user = User.builder().phone(PHONE).firstName("").nickname(PHONE).created(OffsetDateTime.now()).build();
    GetOtpCodeRequest request1 = new GetOtpCodeRequest(PHONE);
    GetOtpCodeRequest request2 = new GetOtpCodeRequest(PHONE);
}