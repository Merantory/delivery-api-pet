package com.merantory.dostavim.dto.mappers.productRestaurant;

import com.merantory.dostavim.dto.impl.productRestaurant.AddProductToRestaurantDto;
import com.merantory.dostavim.dto.impl.productRestaurant.ProductRestaurantDto;
import com.merantory.dostavim.dto.mappers.product.ProductMapper;
import com.merantory.dostavim.dto.mappers.restaurant.RestaurantMapper;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.model.ProductRestaurant;
import com.merantory.dostavim.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ProductRestaurantMapper {
    private final ProductMapper productMapper;
    private final RestaurantMapper restaurantMapper;

    @Autowired
    public ProductRestaurantMapper(@Lazy ProductMapper productMapper, @Lazy RestaurantMapper restaurantMapper) {
        this.productMapper = productMapper;
        this.restaurantMapper = restaurantMapper;
    }

    public ProductRestaurant toProductRestaurant(AddProductToRestaurantDto createProductRestaurantDto) {
        ProductRestaurant productRestaurant = new ProductRestaurant();

        Product product = new Product();
        product.setId(createProductRestaurantDto.getProductId());
        productRestaurant.setProduct(product);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(createProductRestaurantDto.getRestaurantId());
        productRestaurant.setRestaurant(restaurant);

        productRestaurant.setCount(createProductRestaurantDto.getCount());
        return productRestaurant;
    }

    public ProductRestaurantDto toProductRestaurant(ProductRestaurant productRestaurant) {
        ProductRestaurantDto productRestaurantDto = new ProductRestaurantDto();
        if (productRestaurant.getRestaurant() != null) {
            productRestaurantDto.setRestaurantDto(restaurantMapper.toRestaurantDto(productRestaurant.getRestaurant()));
        }
        if (productRestaurant.getProduct() != null) {
            productRestaurantDto.setProductDto(productMapper.toProductDto(productRestaurant.getProduct()));
        }
        productRestaurantDto.setCount(productRestaurant.getCount());
        return productRestaurantDto;
    }

    public Set<ProductRestaurantDto> toProductRestaurantDtoSet(Set<ProductRestaurant> productRestaurantSet) {
        Set<ProductRestaurantDto> productRestaurantDtoSet = new HashSet<>();
        for (ProductRestaurant productRestaurant : productRestaurantSet) {
            productRestaurantDtoSet.add(toProductRestaurant(productRestaurant));
        }
        return productRestaurantDtoSet;
    }


}
