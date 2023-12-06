package com.merantory.dostavim.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@Data
public class Restaurant {
    private Long id;
    private String name;
    private String address;
    private String description;
    private Set<ProductRestaurant> productRestaurantSet;
}
