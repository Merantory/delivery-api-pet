package com.merantory.dostavim.dto.mappers.product;

import com.merantory.dostavim.dto.impl.product.CreateProductDto;
import com.merantory.dostavim.dto.impl.product.ProductDto;
import com.merantory.dostavim.dto.mappers.category.CategoryMapper;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.model.ProductRestaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

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
        if (product.getProductRestaurantSet() != null && !product.getProductRestaurantSet().isEmpty()) {
            Set<ProductRestaurant> productRestaurantSet = product.getProductRestaurantSet();
            Integer count = productRestaurantSet.stream()
                    .filter(pr -> Objects.nonNull(pr.getCount()))
                    .mapToInt(ProductRestaurant::getCount)
                    .sum();
            productDto.setCount(count);
        }

        return productDto;
    }
}
