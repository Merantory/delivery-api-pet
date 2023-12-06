package com.merantory.dostavim.repository.mappers;

import com.merantory.dostavim.model.Category;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.model.ProductRestaurant;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getDouble("price"));
        product.setWeight(rs.getDouble("weight"));
        product.setDescription(rs.getString("description"));
        Category productCategory = new Category();
        productCategory.setName(rs.getString("category"));
        product.setCategory(productCategory);

        try {
            ProductRestaurant productRestaurant = new ProductRestaurant();
            productRestaurant.setCount(rs.getInt("product_count"));
            product.setProductRestaurantSet(new HashSet<>(){{add(productRestaurant);}});
        } catch (SQLException exception) {}

        return product;
    }
}
