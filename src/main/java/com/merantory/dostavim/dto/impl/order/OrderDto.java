package com.merantory.dostavim.dto.impl.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.merantory.dostavim.dto.markerInterfaces.Views;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonView(Views.Public.class)
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
}