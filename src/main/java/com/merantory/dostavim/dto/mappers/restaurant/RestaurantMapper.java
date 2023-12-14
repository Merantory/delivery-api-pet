package com.merantory.dostavim.dto.mappers.restaurant;

import com.merantory.dostavim.dto.impl.restaurant.CreateRestaurantDto;
import com.merantory.dostavim.dto.impl.restaurant.RestaurantDto;
import com.merantory.dostavim.dto.mappers.productRestaurant.ProductRestaurantMapper;
import com.merantory.dostavim.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {
    private final ProductRestaurantMapper productRestaurantMapper;

    @Autowired
    public RestaurantMapper(ProductRestaurantMapper productRestaurantMapper) {
        this.productRestaurantMapper = productRestaurantMapper;
    }

    public Restaurant toRestaurant(CreateRestaurantDto createRestaurantDto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(createRestaurantDto.getName());
        restaurant.setAddress(createRestaurantDto.getAddress());
        restaurant.setDescription(createRestaurantDto.getDescription());

        return restaurant;
    }

    public RestaurantDto toRestaurantDto(Restaurant restaurant) {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setId(restaurant.getId());
        restaurantDto.setName(restaurant.getName());
        restaurantDto.setAddress(restaurant.getAddress());
        restaurantDto.setDescription(restaurant.getDescription());
        if (restaurant.getProductRestaurantSet() != null) {
            restaurantDto.setProductRestaurantDtoSet(productRestaurantMapper
                    .toProductRestaurantDtoSet(restaurant.getProductRestaurantSet()));
        }

        return restaurantDto;
    }
}
