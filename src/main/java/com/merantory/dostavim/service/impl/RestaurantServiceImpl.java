package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.model.ProductRestaurant;
import com.merantory.dostavim.model.Restaurant;
import com.merantory.dostavim.repository.RestaurantRepository;
import com.merantory.dostavim.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Optional<Restaurant> getRestaurant(Long id) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.getRestaurant(id);
        return restaurantOptional;
    }

    @Override
    public Optional<Restaurant> getRestaurantWithProducts(Long id) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.getRestaurantWithProducts(id);
        return restaurantOptional;
    }

    @Override
    public List<Restaurant> getRestaurants(Integer limit, Integer offset) {
        return restaurantRepository.getRestaurants(limit, offset);
    }

    @Override
    @Transactional
    public Restaurant addOrUpdateProduct(ProductRestaurant productRestaurant) {
        Restaurant restaurant = restaurantRepository.addOrUpdateProduct(productRestaurant);
        return restaurant;
    }

    @Override
    @Transactional
    public Restaurant create(Restaurant restaurant) {
        restaurant = restaurantRepository.save(restaurant);
        return restaurant;
    }

    @Override
    @Transactional
    public Restaurant update(Restaurant restaurant) {
        restaurant = restaurantRepository.update(restaurant);
        return restaurant;
    }

    @Override
    @Transactional
    public Restaurant delete(Long id) {
        Restaurant restaurant = restaurantRepository.delete(id);
        return restaurant;
    }
}
