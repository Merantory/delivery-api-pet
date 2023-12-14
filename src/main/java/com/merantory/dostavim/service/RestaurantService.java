package com.merantory.dostavim.service;

import com.merantory.dostavim.model.ProductRestaurant;
import com.merantory.dostavim.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    Optional<Restaurant> getRestaurant(Long id);
    Optional<Restaurant> getRestaurantWithProducts(Long id);
    List<Restaurant> getRestaurants(Integer limit, Integer offset);
    Restaurant addOrUpdateProduct(ProductRestaurant productRestaurant);
    Restaurant create(Restaurant restaurant);
    Restaurant update(Restaurant restaurant);
    Restaurant delete(Long id);
}
