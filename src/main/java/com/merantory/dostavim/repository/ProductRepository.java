package com.merantory.dostavim.repository;

import com.merantory.dostavim.exception.ProductCreationFailedException;
import com.merantory.dostavim.exception.ProductDeleteFailedException;
import com.merantory.dostavim.exception.ProductNotFoundException;
import com.merantory.dostavim.exception.ProductUpdateFailedException;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.repository.mappers.ProductRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.*;

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

    public List<Product> getProductsByIds(Set<Long> ids) {
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sqlQuery = String.format("SELECT * FROM product WHERE id IN (%s)", inSql);
        Long[] idsArray = ids.toArray(new Long[0]);
        return jdbcTemplate.query(sqlQuery, new ProductRowMapper(), idsArray);
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

    public Product save(Product product) {
        String sqlQuery = "INSERT INTO product(name, price, weight, description, category) VALUES(?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[] {"id"});
                ps.setString(1, product.getName());
                ps.setDouble(2, product.getPrice());
                ps.setDouble(3, product.getWeight());
                ps.setString(4, product.getDescription());
                ps.setString(5, product.getCategory().getName());
                return ps;
            }, keyHolder);
            Long productId = Objects.requireNonNull(keyHolder.getKey()).longValue();
            product.setId(productId);
        } catch (DataAccessException e) {
            throw new ProductCreationFailedException();
        }
        return product;
    }

    public Boolean update(Product product) {
        String sqlQuery = "UPDATE product SET name=?, price=?, weight=?, description=?, category=? WHERE id=?";
        Boolean isUpdated = false;
        try {
            isUpdated = (jdbcTemplate.update(sqlQuery, product.getName(), product.getPrice(),
                    product.getWeight(), product.getDescription(), product.getCategory().getName(), product.getId())) != 0;
        } catch (DataAccessException exception) {
            throw new ProductUpdateFailedException();
        }
        if (!isUpdated) {
            throw new ProductNotFoundException(String.format("Product with id %d not found.", product.getId()));
        }
        return isUpdated;
    }

    public Boolean delete(Long id) {
        String sqlQuery = "DELETE FROM \"order\" WHERE id IN (SELECT order_id FROM order_product WHERE product_id=?); " +
                "DELETE FROM order_product WHERE product_id=?; " +
                "DELETE FROM product_restaurant WHERE product_id=?; " +
                "DELETE FROM product WHERE id=?";
        try {
            Boolean isDeleted =  (jdbcTemplate.update(sqlQuery, id, id, id, id)) != 0;
            return isDeleted;
        } catch (DataAccessException exception) {
            throw new ProductDeleteFailedException();
        }
    }
}
