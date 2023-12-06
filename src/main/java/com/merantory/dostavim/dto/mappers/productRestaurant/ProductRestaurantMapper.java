package com.merantory.dostavim.dto.mappers.productRestaurant;

import com.merantory.dostavim.dto.impl.productRestaurant.AddProductToRestaurantDto;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.model.ProductRestaurant;
import com.merantory.dostavim.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class ProductRestaurantMapper {
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
}
