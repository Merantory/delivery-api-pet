package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.product.CreateProductDto;
import com.merantory.dostavim.dto.mappers.product.ProductMapper;
import com.merantory.dostavim.exception.IllegalLimitArgumentException;
import com.merantory.dostavim.exception.IllegalOffsetArgumentException;
import com.merantory.dostavim.exception.ProductNotFoundException;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @Autowired
    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable("id") Long id) {
        Optional<Product> productOptional = productService.getProduct(id);
        if (productOptional.isEmpty()) throw new ProductNotFoundException();
        return new ResponseEntity<>(productMapper.toProductDto(productOptional.get()), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getProducts(@RequestParam(value = "limit") Optional<Integer> limitOptional,
                                         @RequestParam(value = "offset") Optional<Integer> offsetOptional) {
        Integer limit = limitOptional.orElse(1);
        Integer offset = offsetOptional.orElse(0);

        if (limit < 1) throw new IllegalLimitArgumentException();
        if (offset < 0) throw new IllegalOffsetArgumentException();

        return new ResponseEntity<>(productService.getProducts(limit, offset).stream()
                .map(productMapper::toProductDto).toList(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody CreateProductDto createProductDto) {
        Product product = productMapper.toProduct(createProductDto);
        Boolean isCreated = productService.create(product);
        return (isCreated) ? new ResponseEntity<>(HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/{id}/edit")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long id, @RequestBody CreateProductDto createProductDto) {
        Product product = productMapper.toProduct(createProductDto);
        product.setId(id);
        Boolean isUpdated = productService.update(product);
        if (!isUpdated) throw new ProductNotFoundException();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        Boolean isDeleted = productService.delete(id);
        if (!isDeleted) throw new ProductNotFoundException();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
