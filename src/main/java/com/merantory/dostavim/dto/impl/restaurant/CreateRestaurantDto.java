package com.merantory.dostavim.dto.impl.restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRestaurantDto {
    private String name;
    private String address;
    private String description;
}
