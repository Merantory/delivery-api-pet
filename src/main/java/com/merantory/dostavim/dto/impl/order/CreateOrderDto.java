package com.merantory.dostavim.dto.impl.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.merantory.dostavim.dto.impl.orderProduct.CreateOrderProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDto {
    @JsonProperty("restaurant_id")
    private Long restaurantId;
    @JsonProperty("products")
    private Set<CreateOrderProductDto> createOrderProductDtoSet;
}
