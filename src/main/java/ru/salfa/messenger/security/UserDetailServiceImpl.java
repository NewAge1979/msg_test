package ru.salfa.messenger.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.salfa.messenger.entity.postgres.User;
import ru.salfa.messenger.repository.UserRepository;

import java.util.ArrayList;

import static java.time.OffsetDateTime.now;

@Service("userDetailsService")
@Transactional
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneAndIsDeleted(phone, false).orElseThrow(
               () -> new UsernameNotFoundException("User not found.")
        );
        /* Для случая хранения OTP кода в кэше.
        if (user.isEmpty()) {
            user = Optional.of(
                    User.builder()
                            .phone(phone)
                            .firstName("")
                            .nickname("NewUser#" + RandomStringUtils.randomNumeric(9))
                            .created(now())
                            .isDeleted(false)
                            .phoneIsVerified(false)
                            .emailIsConfirmed(false)
                            .build()
            );
            userRepository.save(user.get());
        }*/
        return new org.springframework.security.core.userdetails.User(user.getUsername(), "", new ArrayList<>());
    }
}