package com.merantory.dostavim.dto.impl.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.merantory.dostavim.dto.impl.orderProduct.OrderProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private Double weight;
    private Double cost;
    @JsonProperty("order_date")
    private Instant orderDate;
    @JsonProperty("order_status")
    private String orderStatus;
    @JsonProperty("restaurant_id")
    private Long restaurantId;
    @JsonProperty("person_id")
    private Long personId;
    @JsonProperty("products")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<OrderProductDto> orderProductDtoSet;
}
