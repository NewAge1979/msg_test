package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.salfa.messenger.entity.postgres.LogAccess;

import java.time.OffsetDateTime;

public interface LogAccessRepository extends JpaRepository<LogAccess, Long> {
    @Query(
            value = "Select Count(*) From log_access Where user_id = ?1 And created >= ?2 And created <= ?3 And log_type = 1",
            nativeQuery = true
    )
    int countErrorByUser(long userId, OffsetDateTime date1, OffsetDateTime date2);
}