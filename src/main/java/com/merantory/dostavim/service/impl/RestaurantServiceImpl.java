package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.exception.ProductNotFoundException;
import com.merantory.dostavim.exception.RestaurantNotFoundException;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.model.ProductRestaurant;
import com.merantory.dostavim.model.Restaurant;
import com.merantory.dostavim.repository.ProductRepository;
import com.merantory.dostavim.repository.RestaurantRepository;
import com.merantory.dostavim.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
        log.info("Trying to load restaurant with id={}", id);
        Optional<Restaurant> restaurantOptional = restaurantRepository.getRestaurant(id);
        return restaurantOptional;
    }

    @Override
    public Optional<Restaurant> getRestaurantWithProducts(Long id) {
        log.info("Trying to load restaurant and his products with restaurant_id={}", id);
        Optional<Restaurant> restaurantOptional = restaurantRepository.getRestaurantWithProducts(id);
        return restaurantOptional;
    }

    @Override
    public List<Restaurant> getRestaurants(Integer limit, Integer offset) {
        log.info("Trying to load restaurants with limit={} and offset={}", limit, offset);
        return restaurantRepository.getRestaurants(limit, offset);
    }

    @Override
    @Transactional
    public Restaurant addOrUpdateProduct(ProductRestaurant productRestaurant) {
        log.info("Trying to add or update product for restaurant with id={}", productRestaurant.getRestaurant().getId());
        if (!isExistProduct(productRestaurant.getProduct())) {
            log.info("Product with id={} not found", productRestaurant.getRestaurant().getId());
            throw new ProductNotFoundException(String.format("Product with id %d not found.",
                    productRestaurant.getProduct().getId()));
        }
        if (!isExistRestaurant(productRestaurant.getRestaurant())) {
            log.info("Restaurant with id={} not found", productRestaurant.getRestaurant().getId());
            throw new RestaurantNotFoundException(String.format("Restaurant with id %d not found.",
                    productRestaurant.getRestaurant().getId()));
        }
        Restaurant restaurant = restaurantRepository.addOrUpdateProduct(productRestaurant);
        log.info("ProductsRestaurant count was updated for product with id={} and restaurant with id={}",
                productRestaurant.getProduct().getId(), productRestaurant.getRestaurant().getId());
        return restaurant;
    }

    @Override
    @Transactional
    public Restaurant create(Restaurant restaurant) {
        log.info("Trying to create restaurant: {}", restaurant);
        restaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant has been created: {}", restaurant);
        return restaurant;
    }

    @Override
    @Transactional
    public Restaurant update(Restaurant restaurant) {
        log.info("Trying to update restaurant with id={} on new data={}", restaurant.getId(), restaurant);
        restaurant = restaurantRepository.update(restaurant);
        log.info("Restaurant has been updated: {}", restaurant);
        return restaurant;
    }

    @Override
    @Transactional
    public Restaurant delete(Long id) {
        log.info("Trying to delete restaurant with id={}", id);
        if (!isExistRestaurant(id)) {
            log.info("Restaurant with id={} not found", id);
            throw new RestaurantNotFoundException(String.format("Restaurant with id %d not found.", id));
        }
        Restaurant restaurant = restaurantRepository.delete(id);
        log.info("Restaurant has been deleted: {}", restaurant);
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
