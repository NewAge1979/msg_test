package ru.salfa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.salfa.messenger.entity.postgres.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneAndIsDeleted(String phone, boolean isDeleted);
    Optional<User> findByIdAndIsDeleted(Long id, boolean isDeleted);
    List<User> findByNicknameContainingIgnoreCaseAndIsDeleted(String nickname, boolean isDeleted);

    boolean existsByPhone(String phone);
}