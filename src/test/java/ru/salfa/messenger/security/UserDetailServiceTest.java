package ru.salfa.messenger.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.salfa.messenger.entity.postgres.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.salfa.messenger.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserDetailServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailServiceImpl userDetailService;

    private final String PHONE = "0000000000";
    User user = User.builder().phone(PHONE).firstName("").nickname(PHONE).created(OffsetDateTime.now()).build();

    @Test
    void loadUserByUsernameSuccess() {
        given(userRepository.findByPhoneAndIsDeleted(PHONE, false)).willReturn(Optional.of(user));
        assertDoesNotThrow(() -> userDetailService.loadUserByUsername(PHONE));
    }

    @Test
    void loadUserByUsernameError() {
        given(userRepository.findByPhoneAndIsDeleted(PHONE, false)).willReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userDetailService.loadUserByUsername(PHONE));
    }
}