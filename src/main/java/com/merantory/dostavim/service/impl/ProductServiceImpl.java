package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.repository.ProductRepository;
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

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
        return productRepository.getRestaurantProducts(restaurantId, limit, offset);
    }

    @Override
    @Transactional
    public Product create(Product product) {
        product = productRepository.save(product);
        return product;
    }

    @Override
    @Transactional
    public Product update(Product product) {
        product = productRepository.update(product);
        return product;
    }

    @Override
    @Transactional
    public Product delete(Long id) {
        Product product = productRepository.delete(id);
        return product;
    }
}
