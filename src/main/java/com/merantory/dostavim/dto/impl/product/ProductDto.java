package com.merantory.dostavim.dto.impl.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.merantory.dostavim.dto.impl.category.CategoryDto;
import com.merantory.dostavim.dto.markerInterfaces.Views;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonView(Views.Public.class)
public class ProductDto {
    private Long id;
    private String name;
    private Double price;
    private Double weight;
    @JsonView(Views.Exclusive.class)
    private String description;
    @JsonView(Views.Exclusive.class)
    private CategoryDto category;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;
}
