package com.merantory.dostavim.dto.impl.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.merantory.dostavim.dto.impl.category.CreateCategoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDto {
    private String name;
    private Double price;
    private Double weight;
    private String description;
    @JsonProperty("category")
    private CreateCategoryDto createCategoryDto;
}
