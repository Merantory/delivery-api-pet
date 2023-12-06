package com.merantory.dostavim.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@Data
public class Product {
    private Long id;
    private String name;
    private Double price;
    private Double weight;
    private String description;
    private Category category;
    private Set<OrderProduct> orderProductSet;
    private Set<ProductRestaurant> productRestaurantSet;
}
