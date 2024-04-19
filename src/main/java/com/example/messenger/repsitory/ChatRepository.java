package com.example.messenger.repsitory;

import com.example.messenger.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для чата
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat,Long> {
}
