package ru.salfa.messenger.dto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDto {
    private Long id;
    private String phone;
    private String firstName;
    private String lastName;
    private String email;
    private String nickname;
}
