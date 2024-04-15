package com.example.messenger.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

/**
* User - сущность
 * <br><i>содержит поля:</i>
 * <br>- id <i>(id пользователя)</i>;
 * <br>- lastLogin <i>(последняя авторизация)</i>;
 * <br>- isSuperuser <i>(является ли пользователь администратором)</i>;
 * <br>- email <i>(почта пользователя)</i>;
 * <br>- isStaff <i>(является ли персоналом)</i>;
 * <br>- isActive <i>(активен ли пользователь)</i>;
 * <br>- dataJoined <i>(дата присоединения)</i>;
 * <br>- phone <i>(телефон пользователя)</i>;
 * <br>- firstName <i>(имя пользователя)</i>;
 * <br>- lastName <i>(фамилия пользователя)</i>;
 * <br>- nickname <i>(никнейм пользователя)</i>;
 * <br>- personalInfo <i>(персональная информация пользователя)</i>;
 * <br>- avatar <i>(аватар пользователя)</i>;
 * <br>- isVerified <i>(верифицирован ли пользователь)</i>;
 * <br>- otpCode <i>(отп код пользователя)</i>;
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    /**
     * id пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * последняя авторизация
     */
    private String lastLogin;
    /**
     * является ли пользователь администратором
     */
    private boolean isSuperuser;
    /**
     * почта пользователя
     */
    private String email;
    /**
     * является ли персоналом
     */
    private boolean isStaff;
    /**
     * активен ли пользователь
     */
    private boolean isActive;
    /**
     * дата присоединения
     */
    private String dateJoined;
    /**
     * телефон пользователя
     */
    private String phone;
    /**
     * имя пользователя
     */
    private String firstName;
    /**
     * фамилия пользователя
     */
    private String lastName;
    /**
     * никнейм пользователя
     */
    private String nickname;
    /**
     * персональная информация пользователя
     */
    private String personalInfo;
    /**
     * аватар пользователя
     */
    private String avatar;
    /**
     * верифицирован ли пользователь
     */
    private boolean isVerified;
    /**
     * отп код пользователя
     */
    private String otpCode;
}
