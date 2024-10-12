package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.salfa.messenger.entity.postgres.Chat;
import ru.salfa.messenger.entity.postgres.Messages;

import java.util.List;

public interface MessageRepository extends JpaRepository<Messages, Long> {
    List<Messages> findMessagesByChatId(Chat chat);

    List<Messages> findAllByChatId_Id(Long chatId);

    @Query(value = """
            SELECT m.id, m.message, m.chat_id, m.sender_id, m.is_delete, m.is_read, m.type, m.created, m.modified
            FROM message m
            LEFT JOIN message_user_delete mud ON m.id = mud.message_id
            LEFT JOIN user u ON mud.user_id = u.id
            WHERE m.chat_id = :chatId
            AND m.is_delete = false
            AND (u.phone IS NULL OR u.phone != :userPhone)
            ORDER BY DATE(m.created), m.created;
            """, nativeQuery = true)
    List<Messages> findAllByChatIdWithFilter( Long chatId, String userPhone);

}
