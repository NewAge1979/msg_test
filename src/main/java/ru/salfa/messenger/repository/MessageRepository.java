package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.salfa.messenger.entity.postgres.Chat;
import ru.salfa.messenger.entity.postgres.Messages;

import java.util.List;

public interface MessageRepository extends JpaRepository<Messages, Long> {
    List<Messages> findMessagesByChatId(Chat chat);

    @Query(value = """
            SELECT m.*
            FROM messages m
            LEFT JOIN messages_user_delete_message mud ON m.id = mud.messages_id
            LEFT JOIN users u ON mud.user_delete_message_id = u.id
            WHERE m.chat_id = :chatId
            AND m.is_delete = false
            AND (u.phone IS NULL OR u.phone != :userPhone)
            ORDER BY DATE(m.created), m.created;
            """, nativeQuery = true)
    List<Messages> findAllByChatIdWithFilter(Long chatId, String userPhone);

}
