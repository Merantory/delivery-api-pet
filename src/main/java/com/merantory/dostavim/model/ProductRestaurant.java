package com.merantory.dostavim.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProductRestaurant {
    private Product product;
    private Restaurant restaurant;
    private Integer count;
}
