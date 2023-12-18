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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
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
        Optional<Product> productOptional = productRepository.getProduct(id);
        return productOptional;
    }

    @Override
    public List<Product> getProducts(Integer limit, Integer offset) {
        return productRepository.getProducts(limit, offset);
    }

    @Override
    public List<Product> getRestaurantProducts(Long restaurantId, Integer limit, Integer offset) {
        if (!isExistRestaurant(restaurantId)) {
            throw new RestaurantNotFoundException(String.format("Restaurant with id %d not found.", restaurantId));
        }
        return productRepository.getRestaurantProducts(restaurantId, limit, offset);
    }

    @Override
    @Transactional
    public Product create(Product product) {
        if (!isExistCategory(product.getCategory())) {
            throw new CategoryNotExistException(String.format("Category with name '%s' doesnt exist.",
                    product.getCategory().getName()));
        }
        product = productRepository.save(product);
        return product;
    }

    @Override
    @Transactional
    public Product update(Product product) {
        if (!isExistCategory(product.getCategory())) {
            throw new CategoryNotExistException(String.format("Category with name '%s' doesnt exist.",
                    product.getCategory().getName()));
        }
        Boolean isUpdated = productRepository.update(product);
        if (!isUpdated) {
            throw new ProductNotFoundException(String.format("Product with id %d not found.", product.getId()));
        }
        Optional<Product> updatedProduct = productRepository.getProduct(product.getId());
        return updatedProduct.get();
    }

    @Override
    @Transactional
    public Product delete(Long id) {
        Optional<Product> productOptional = getProduct(id);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException(String.format("Product with id %d not found.", id));
        }
        Boolean isDeleted = productRepository.delete(id);
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
}