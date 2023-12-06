package com.merantory.dostavim.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@NoArgsConstructor
@Data
public class Order {
    private Long id;
    private Double weight;
    private Double cost;
    private Instant orderDate;
    private String orderStatus;
    private Restaurant restaurant;
    private Person person;
    private Set<OrderProduct> orderProductSet;
}
