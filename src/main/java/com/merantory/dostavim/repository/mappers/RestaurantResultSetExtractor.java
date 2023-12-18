package com.merantory.dostavim.repository.mappers;

import com.merantory.dostavim.model.Category;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.model.ProductRestaurant;
import com.merantory.dostavim.model.Restaurant;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RestaurantResultSetExtractor implements ResultSetExtractor<List<Restaurant>> {
    @Override
    public List<Restaurant> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Restaurant> idRestaurantMap = new HashMap<>();
        while (rs.next()) {
            Long restaurantId = rs.getLong("restaurant_id");
            if (!idRestaurantMap.containsKey(restaurantId)) {
                Restaurant restaurant = new Restaurant();
                restaurant.setId(rs.getLong("restaurant_id"));
                restaurant.setName(rs.getString("restaurant_name"));
                restaurant.setAddress(rs.getString("restaurant_address"));
                restaurant.setDescription(rs.getString("restaurant_description"));



                idRestaurantMap.put(restaurantId, restaurant);
            }
            Product product = new Product();
            Category productCategory = new Category();
            productCategory.setName(rs.getString("product_category"));
            product.setId(rs.getLong("product_id"));
            product.setName(rs.getString("product_name"));
            product.setPrice(rs.getDouble("product_price"));
            product.setWeight(rs.getDouble("product_weight"));
            product.setDescription(rs.getString("product_description"));
            product.setCategory(productCategory);

            ProductRestaurant productRestaurant = new ProductRestaurant();
            productRestaurant.setProduct(product);
            productRestaurant.setCount(rs.getInt("product_count"));

            if (idRestaurantMap.get(restaurantId).getProductRestaurantSet() == null) {
                idRestaurantMap.get(restaurantId).setProductRestaurantSet(new HashSet<>());
            }
            idRestaurantMap.get(restaurantId).getProductRestaurantSet().add(productRestaurant);
        }
        return new ArrayList<>(idRestaurantMap.values());
    }
}
