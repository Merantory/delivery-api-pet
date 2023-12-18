package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.exception.CategoryNotExistException;
import com.merantory.dostavim.exception.ProductNotFoundException;
import com.merantory.dostavim.exception.RestaurantNotFoundException;
import com.merantory.dostavim.model.Category;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.model.Restaurant;
import com.merantory.dostavim.repository.CategoryRepository;
import com.merantory.dostavim.repository.ProductRepository;
import com.merantory.dostavim.repository.RestaurantRepository;
import com.merantory.dostavim.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,
                              RestaurantRepository restaurantRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        log.info("Trying to load product with id: {}", id);
        Optional<Product> productOptional = productRepository.getProduct(id);
        return productOptional;
    }

    @Override
    public List<Product> getProducts(Integer limit, Integer offset) {
        log.info("Trying to load products with limit={} and offset={}", limit, offset);
        return productRepository.getProducts(limit, offset);
    }

    @Override
    public List<Product> getRestaurantProducts(Long restaurantId, Integer limit, Integer offset) {
        log.info("Trying to load restaurant products with restaurant_id={} with limit={} and offset={}",
                restaurantId, limit, offset);
        if (!isExistRestaurant(restaurantId)) {
            log.info("Restaurant with id={} not found", restaurantId);
            throw new RestaurantNotFoundException(String.format("Restaurant with id %d not found.", restaurantId));
        }
        return productRepository.getRestaurantProducts(restaurantId, limit, offset);
    }

    @Override
    @Transactional
    public Product create(Product product) {
        log.info("Trying to create product: {}", product);
        if (!isExistCategory(product.getCategory())) {
            log.info("Category with name={} doesnt exist", product.getCategory().getName());
            throw new CategoryNotExistException(String.format("Category with name '%s' doesnt exist.",
                    product.getCategory().getName()));
        }
        product = productRepository.save(product);
        return product;
    }

    @Override
    @Transactional
    public Product update(Product product) {
        log.info("Trying to update product with id={} on new data={}", product.getId(), product);
        if (!isExistProduct(product)) {
            log.info("Product with id={} not found", product.getId());
            throw new ProductNotFoundException(String.format("Product with id %d not found.", product.getId()));
        }
        if (!isExistCategory(product.getCategory())) {
            log.info("Category with name={} doesnt exist", product.getCategory().getName());
            throw new CategoryNotExistException(String.format("Category with name '%s' doesnt exist.",
                    product.getCategory().getName()));
        }
        Boolean isUpdated = productRepository.update(product);
        Optional<Product> updatedProduct = productRepository.getProduct(product.getId());
        log.info("Product has been updated: {}", updatedProduct.get());
        return updatedProduct.get();
    }

    @Override
    @Transactional
    public Product delete(Long id) {
        log.info("Trying to delete product with id={}", id);
        Optional<Product> productOptional = getProduct(id);
        if (productOptional.isEmpty()) {
            log.info("Product with id={} not found", id);
            throw new ProductNotFoundException(String.format("Product with id %d not found.", id));
        }
        Boolean isDeleted = productRepository.delete(id);
        log.info("Product has been deleted with data={}", productOptional.get());
        return productOptional.get();
    }

    private Boolean isExistCategory(Category category) {
        return categoryRepository.isExistCategory(category);
    }

    private Boolean isExistRestaurant(Restaurant restaurant) {
        return isExistRestaurant(restaurant.getId());
    }

    private Boolean isExistRestaurant(Long restaurantId) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.getRestaurant(restaurantId);
        return restaurantOptional.isPresent();
    }

    private Boolean isExistProduct(Product product) {
        return isExistProduct(product.getId());
    }

    private Boolean isExistProduct(Long id) {
        Optional<Product> productOptional = productRepository.getProduct(id);
        return productOptional.isPresent();
    }
}