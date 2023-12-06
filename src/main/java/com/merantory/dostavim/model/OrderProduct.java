package com.merantory.dostavim.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OrderProduct {
    private Order order;
    private Product product;
    private Integer count;
}
