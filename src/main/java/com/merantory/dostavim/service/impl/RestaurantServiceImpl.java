package com.merantory.dostavim.service.impl;

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
    public List<Restaurant> getRestaurants(Integer limit, Integer offset) {
        return restaurantRepository.getRestaurants(limit, offset);
    }

    @Override
    @Transactional
    public Boolean create(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    public Boolean update(Restaurant restaurant) {
        return restaurantRepository.update(restaurant);
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        return restaurantRepository.delete(id);
    }
}
