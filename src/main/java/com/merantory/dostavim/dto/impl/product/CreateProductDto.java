package com.merantory.dostavim.dto.impl.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.merantory.dostavim.dto.impl.category.CreateCategoryDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDto {
    @NotEmpty(message = "Значение поля не должно быть пустым")
    @Size(min = 2, message = "Поле не может быть короче 2 символов")
    private String name;

    @NotNull(message = "Значение поля не должно быть пустым")
    @PositiveOrZero(message = "Значение поля не должно быть отрицательным")
    private Double price;

    @NotNull(message = "Значение поля не должно быть пустым")
    @PositiveOrZero(message = "Значение поля не должно быть отрицательным")
    private Double weight;

    @NotEmpty(message = "Значение поля не должно быть пустым")
    private String description;

    @JsonProperty("category")
    private @Valid CreateCategoryDto createCategoryDto;
}
