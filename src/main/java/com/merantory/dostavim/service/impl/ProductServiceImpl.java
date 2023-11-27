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
    @Transactional
    public Boolean create(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Boolean update(Product product) {
        return productRepository.update(product);
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        return productRepository.delete(id);
    }
}
