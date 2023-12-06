package com.merantory.dostavim.dto.impl.productRestaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductToRestaurantDto {
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("restaurant_id")
    private Long restaurantId;
    private Integer count;
}
