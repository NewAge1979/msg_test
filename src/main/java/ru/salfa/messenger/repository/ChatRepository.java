package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.salfa.messenger.entity.Chat;
import ru.salfa.messenger.entity.User;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findChatsByParticipantsContaining(User user);
}
