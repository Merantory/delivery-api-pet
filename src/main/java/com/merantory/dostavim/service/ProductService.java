package com.merantory.dostavim.service;

import com.merantory.dostavim.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Optional<Product> getProduct(Long id);
    List<Product> getProducts(Integer limit, Integer offset);
    List<Product> getRestaurantProducts(Long restaurantId, Integer limit, Integer offset);
    Product create(Product product);
    Product update(Product product);
    Product delete(Long id);
}
