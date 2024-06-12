package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.salfa.messenger.entity.NewUser;

public interface NewUserRepository extends JpaRepository<NewUser, Long> {
    boolean existsById(Long userId);
}