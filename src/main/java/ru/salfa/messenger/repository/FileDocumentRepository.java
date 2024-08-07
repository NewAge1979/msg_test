package ru.salfa.messenger.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.salfa.messenger.entity.FileDocument;

@Repository
public interface FileDocumentRepository extends MongoRepository<FileDocument, String> {
}
