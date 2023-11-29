package com.merantory.dostavim.service;

import com.merantory.dostavim.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    Optional<Restaurant> getRestaurant(Long id);
    List<Restaurant> getRestaurants(Integer limit, Integer offset);
    Boolean create(Restaurant restaurant);
    Boolean update(Restaurant restaurant);
    Boolean delete(Long id);
}
