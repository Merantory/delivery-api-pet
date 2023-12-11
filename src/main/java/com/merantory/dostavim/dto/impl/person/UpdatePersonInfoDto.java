package com.merantory.dostavim.dto.impl.person;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePersonInfoDto {
    @NotEmpty(message = "Значение поля не должно быть пустым")
    @Size(min = 2, message = "Поле не может быть короче 2 символов")
    private String name;

    @NotEmpty(message = "Значение поля не должно быть пустым")
    private String address;
}
