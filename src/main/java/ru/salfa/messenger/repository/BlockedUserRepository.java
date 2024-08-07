package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.salfa.messenger.entity.postgres.BlockedUser;

public interface BlockedUserRepository extends JpaRepository<BlockedUser, Long> {
    boolean existsByUserId(Long userId);
}