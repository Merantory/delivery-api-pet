package com.merantory.dostavim.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Product {
    private Long id;
    private String name;
    private Double price;
    private Double weight;
    private String description;
    private Category category;
}
