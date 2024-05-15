package ru.salfa.messenger.repsitory;

import ru.salfa.messenger.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для сообщений
 */
public interface MessageRepository extends JpaRepository<Message,Long> {
}
