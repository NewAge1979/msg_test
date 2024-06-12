package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.salfa.messenger.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    //Select id From users Where phone = '9999999999' And phone_is_verified = false
}