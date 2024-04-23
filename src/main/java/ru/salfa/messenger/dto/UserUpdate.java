package ru.salfa.messenger.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

/**
 *UserUpdate обновление данных пользователя
 * <br><i>содержит поля:</i>
 * <br>- firstName <i>(имя пользователя)</i>
 * <br>- lastName <i>(фамилия пользователя)</i>
 * <br>- nickName <i>(прозвище пользователя)</i>
 * <br>- personalInformation <i>(информация о себе)</i>
 */
@Data
public class UserUpdate {
    /**
     * Имя пользователя
     */
    @Size(min = 1,max = 100)
    @Schema(description = "Имя пользователя")
    @NonNull
    private String firstName;
    /**
     * Фамилия пользователя
     */
    @Size(min = 1,max = 100)
    @Schema(description = "Фамилия пользователя")
    @NonNull
    private String lastName;
    /**
     * Прозвище пользователя
     */
    @Size(min = 1,max = 100)
    @Schema(description = "Прозвище пользователя")
    @NonNull
    private String nickname;
    /**
     * Информация о себе
     */
    @Size(min = 1,max = 500)
    @Schema(description = "Информация о себе")
    private String personalInformation;
}
