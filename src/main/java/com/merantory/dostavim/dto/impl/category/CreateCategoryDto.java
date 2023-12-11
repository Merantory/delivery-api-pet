package com.merantory.dostavim.dto.impl.category;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryDto {
    @NotEmpty(message = "Значение поля не должно быть пустым")
    @Pattern(regexp = "[a-zA-Z]+", message = "Поле содержит запрещенные символы")
    private String name;
}
