package com.merantory.dostavim.dto.impl.productRestaurant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.merantory.dostavim.dto.impl.product.ProductDto;
import com.merantory.dostavim.dto.impl.restaurant.RestaurantDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductRestaurantDto {
    @JsonProperty("restaurant")
    private RestaurantDto restaurantDto;
    @JsonProperty("product")
    private ProductDto productDto;
    private Integer count;
}
