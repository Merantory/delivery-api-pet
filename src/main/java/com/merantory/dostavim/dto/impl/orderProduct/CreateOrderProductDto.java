package com.merantory.dostavim.dto.impl.orderProduct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderProductDto {
    @JsonProperty("product_id")
    private Long productId;
    private String name;
    private Double price;
    private Double weight;
    private Integer count;
}
