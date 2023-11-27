package com.merantory.dostavim.dto.mappers.product;

import com.merantory.dostavim.dto.impl.product.CreateProductDto;
import com.merantory.dostavim.dto.impl.product.ProductDto;
import com.merantory.dostavim.dto.mappers.category.CategoryMapper;
import com.merantory.dostavim.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    private final CategoryMapper categoryMapper;

    @Autowired
    public ProductMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public Product toProduct(CreateProductDto createProductDto) {
        Product product = new Product();
        product.setName(createProductDto.getName());
        product.setPrice(createProductDto.getPrice());
        product.setWeight(createProductDto.getWeight());
        product.setDescription(createProductDto.getDescription());
        product.setCategory(categoryMapper.toCategory(createProductDto.getCreateCategoryDto()));

        return product;
    }

    public ProductDto toProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        productDto.setWeight(product.getWeight());
        productDto.setDescription(product.getDescription());
        productDto.setCategory(categoryMapper.toCategoryDto(product.getCategory()));

        return productDto;
    }
}
