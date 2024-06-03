package ru.salfa.messenger.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.salfa.messenger.entity.User;
import ru.salfa.messenger.repository.UserRepository;

import java.util.Optional;

@Service("userDetailsService")
@Transactional
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByPhone(phone);
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}