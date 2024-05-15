package ru.salfa.messenger.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * ChatCreate - создатель чата
 *  <br><i>содержит поля:</i>
 *  <br>- creator <i>(создатель)</i>
 */
@Data
public class ChatCreate {
    /**
     * Создатель
     */
    @Schema(description = "Создатель")
    private Long creator;
}
