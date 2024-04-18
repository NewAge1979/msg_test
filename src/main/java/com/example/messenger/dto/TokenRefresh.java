package com.example.messenger.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;
/**
 * TokenRefresh - обновления токена
 * <br><i>содержит поля:</i>
 * <br>- refresh <i>(обновить)</i>
 * <br>- access <i>(доступ)</i>
 */
@Data
public class TokenRefresh {
    /**
     * Обновить
     */
    @Size(min = 1)
    @Schema(description = "Refresh")
    private String refresh;
    /**
     * Доступ
     */
    @Schema(description = "Access")
    @Size(min = 1)
    private final String access;
}
