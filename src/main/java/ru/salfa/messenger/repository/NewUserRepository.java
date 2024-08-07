package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.salfa.messenger.entity.postgres.NewUser;

public interface NewUserRepository extends JpaRepository<NewUser, Long> {
    boolean existsById(Long userId);
}