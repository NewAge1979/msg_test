package ru.salfa.messenger.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Message - сущность сообщение
 * <br><i>содержит поля:</i>
 * <br>- id <i>(id сообщения)</i>;
 * <br>- text <i>(текст сообщения)</i>;
 * <br>- attachment <i>(вложение)</i>;
 * <br>- created <i>(дата и время создания сообщения)</i>;
 * <br>- update <i>(дата и время редактирования)</i>;
 * <br>- sender <i>(отправитель)</i>;
 * <br>- chat <i>(чат)</i>;
 */
@Entity
@Data
public class Message {
    /**
     * ID сообщения
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID")
    private Long id;
    /**
     * Текст сообщения
     */
    @Size(min = 1)
    @Schema(description = "Текст сообщения")
    private String text;
    /**
     * Вложение
     */
    @Schema(description = "Attachment")
    @ElementCollection
    private List<URI> attachment;
    /**
     * Дата и время создания сообщения
     */
    @Schema(description = "Дата и время создания сообщения")
    private LocalDateTime created;
    /**
     * Дата и время редактирования
     */
    @Schema(description = "Дата и время редактирования")
    private LocalDateTime update;
    /**
     * Отправитель
     */
    @Schema(description = "Отправитель")
    private Long sender;
    /**
     * Чат
     */
    @Schema(description = "Чат")
    private Long chat;
}
