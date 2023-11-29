package com.merantory.dostavim.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Restaurant {
    private Long id;
    private String name;
    private String address;
    private String description;
}
