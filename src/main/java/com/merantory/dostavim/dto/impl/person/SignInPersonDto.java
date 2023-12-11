package com.merantory.dostavim.dto.impl.person;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInPersonDto {
    @NotEmpty(message = "Значение поля не должно быть пустым")
    @Email(message = "Полученное значение не является электронной почтой")
    private String email;
    @NotEmpty(message = "Значение поля не должно быть пустым")
    @Size(min = 6, message = "Пароль не может быть короче 6 символов")
    private String password;
}
