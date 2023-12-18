package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.exception.ProductNotFoundException;
import com.merantory.dostavim.exception.RestaurantNotFoundException;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.model.ProductRestaurant;
import com.merantory.dostavim.model.Restaurant;
import com.merantory.dostavim.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, ProductRepository productRepository) {
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
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
        if (!isExistProduct(productRestaurant.getProduct())) {
            throw new ProductNotFoundException(String.format("Product with id %d not found.",
                    productRestaurant.getProduct().getId()));
        }
        if (!isExistRestaurant(productRestaurant.getRestaurant())) {
            throw new RestaurantNotFoundException(String.format("Restaurant with id %d not found.",
                    productRestaurant.getRestaurant().getId()));
        }
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
        if (!isExistRestaurant(id)) {
            throw new RestaurantNotFoundException(String.format("Restaurant with id %d not found.", id));
        }
        Restaurant restaurant = restaurantRepository.delete(id);
        return restaurant;
    }

    private Boolean isExistProduct(Product product) {
        return isExistProduct(product.getId());
    }

    private Boolean isExistProduct(Long productId) {
        Optional<Product> productOptional = productRepository.getProduct(productId);
        return productOptional.isPresent();
    }

    private Boolean isExistRestaurant(Restaurant restaurant) {
        return isExistRestaurant(restaurant.getId());
    }

    private Boolean isExistRestaurant(Long restaurantId) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.getRestaurant(restaurantId);
        return restaurantOptional.isPresent();
    }
}
