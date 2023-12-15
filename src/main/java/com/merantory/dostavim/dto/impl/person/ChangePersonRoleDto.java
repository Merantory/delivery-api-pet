package com.merantory.dostavim.dto.impl.person;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePersonRoleDto {
    @NotNull(message = "Значение поля не должно быть пустым")
    @Positive(message = "Значение поля должно быть положительным")
    private Long id;

    @NotEmpty(message = "Значение поля не должно быть пустым")
    private String role;
}
