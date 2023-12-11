package com.merantory.dostavim.dto.impl.restaurant;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRestaurantDto {
    @NotEmpty(message = "Значение поля не должно быть пустым")
    private String name;
    @NotEmpty(message = "Значение поля не должно быть пустым")
    private String address;
    @NotEmpty(message = "Значение поля не должно быть пустым")
    private String description;
}
