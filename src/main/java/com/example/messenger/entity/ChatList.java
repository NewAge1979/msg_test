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
 * <br>- created <i>(время и дата создания чата)</i>;
 * <br>- creator <i>(создатель)</i>;
 * <br>- participants <i>(участники)</i>;
 */
@Entity
@Data
public class ChatList {
    /**
     * ID чата
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID")
    private Integer id;
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
    private Set<Integer> participants;
}
