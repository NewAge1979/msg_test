package ru.salfa.messenger.repsitory;

import ru.salfa.messenger.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для чата
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat,Long> {
}
