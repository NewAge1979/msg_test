package com.example.messenger.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;
/**
 * ChatList-сущность чат
 * <br><i>содержит поля:</i>
 * <br>- id <i>(id чата)</i>;
 * <br>- title <i>(заголовок)</i>;
 * <br>- description <i>(описание)</i>;
 * <br>- created <i>(время и дата создания чата)</i>;
 * <br>- creator <i>(создатель)</i>;
 * <br>- users <i>(участники)</i>;
 */
@Entity
@Data
public class Chat {
    /**
     * ID чата
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID")
    private Long id;
    /**
     * Заголовок
     */
    @Schema(description = "Заголовок")
    private String title;
    /**
     * Описание
     */
    @Schema(description = "Описание")
    private String description;
    /**
     * Время и дата создания чата
     */
    @Schema(description = "Время и дата создания чата")
    private LocalDateTime created;
    /**
     * Создатель
     */
    @Schema(description = "Создатель")
    private String creator;
    /**
     * Участники
     */
    @ElementCollection
    @Schema(description = "Участники")
    private Set<Long> users;
}
