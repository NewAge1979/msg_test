package ru.salfa.messenger.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.salfa.messenger.entity.Attachments;

public interface AttachmentsRepository extends JpaRepository<Attachments, Long> {
}
