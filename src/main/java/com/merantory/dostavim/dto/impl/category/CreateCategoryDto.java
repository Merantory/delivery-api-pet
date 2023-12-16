package com.merantory.dostavim.dto.impl.category;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+(?:\\s[a-zA-Zа-яА-Я]+)*$", message = "Поле содержит запрещенные символы")
    @Schema(type = "string", example = "Бакалея")
    private String name;
}
