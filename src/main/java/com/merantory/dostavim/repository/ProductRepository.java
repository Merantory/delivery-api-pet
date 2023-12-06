package com.merantory.dostavim.repository;

import com.merantory.dostavim.exception.ProductNotFoundException;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.exception.CategoryNotExistException;
import com.merantory.dostavim.repository.mappers.ProductRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Product> getProduct(Long id) {
        String sqlQuery = "SELECT * FROM product WHERE id=?";
        Optional<Product> productOptional;
        try {
            productOptional = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, new ProductRowMapper(), id));
        } catch (EmptyResultDataAccessException emptyException) {
            productOptional = Optional.empty();
        }
        return productOptional;
    }

    public List<Product> getProducts(Integer limit, Integer offset) {
        String sqlQuery = "SELECT * FROM product LIMIT ? OFFSET ?";
        List<Product> productList = jdbcTemplate.query(sqlQuery, new ProductRowMapper(), limit, offset);
        return productList;
    }

    public List<Product> getRestaurantProducts(Long restaurantId, Integer limit, Integer offset) {
        String sqlQuery = "SELECT " +
                "id, name, price, weight, description, category, product_count " +
                "FROM (SELECT product_id, count AS product_count FROM product_restaurant " +
                    "WHERE restaurant_id=? LIMIT ? OFFSET ?) AS pr " +
                "JOIN product AS p ON p.id = pr.product_id";
        List<Product> productList = jdbcTemplate.query(sqlQuery, new ProductRowMapper(), restaurantId, limit, offset);
        return productList;
    }

    public Boolean save(Product product) {
        String sqlQuery = "INSERT INTO product(name, price, weight, description, category) VALUES(?, ?, ?, ?, ?)";
        Boolean isSaved = false;
        try {
            isSaved = (jdbcTemplate.update(sqlQuery, product.getName(), product.getPrice(), product.getWeight(),
                    product.getDescription(), product.getCategory().getName())) != 0;
        } catch (DataAccessException e) {
            throw new CategoryNotExistException();
        }
        return isSaved;
    }

    public Boolean update(Product product) {
        String sqlQuery = "UPDATE product SET name=?, price=?, weight=?, description=?, category=? WHERE id=?";
        Boolean isUpdated = (jdbcTemplate.update(sqlQuery, product.getName(), product.getPrice(),
                product.getWeight(), product.getDescription(), product.getCategory().getName(), product.getId())) != 0;
        return isUpdated;
    }

    public Boolean delete(Long id) {
        String sqlQuery = "DELETE FROM product WHERE id=?";
        Boolean isDeleted = (jdbcTemplate.update(sqlQuery, id)) != 0;
        return isDeleted;
    }
}
