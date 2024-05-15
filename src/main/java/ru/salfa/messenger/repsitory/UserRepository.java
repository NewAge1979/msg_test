package ru.salfa.messenger.repsitory;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.salfa.messenger.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
