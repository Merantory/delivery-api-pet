package com.merantory.dostavim.dto.impl.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.merantory.dostavim.dto.impl.category.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private Double price;
    private Double weight;
    private String description;
    private CategoryDto category;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;
}
