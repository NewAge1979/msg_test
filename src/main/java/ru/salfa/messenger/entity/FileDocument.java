package ru.salfa.messenger.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "files")
@Getter
@Setter
@AllArgsConstructor
public class FileDocument {
    @Id
    private String id;
    private String url;
}
