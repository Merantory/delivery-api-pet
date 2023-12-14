package com.merantory.dostavim.repository;

import com.merantory.dostavim.exception.*;
import com.merantory.dostavim.model.OrderProduct;
import com.merantory.dostavim.model.ProductRestaurant;
import com.merantory.dostavim.model.Restaurant;
import com.merantory.dostavim.repository.mappers.RestaurantResultSetExtractor;
import com.merantory.dostavim.repository.mappers.RestaurantRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
public class RestaurantRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RestaurantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Restaurant> getRestaurant(Long id) {
        String sqlQuery = "SELECT * FROM restaurant WHERE id=?";
        Optional<Restaurant> restaurantOptional;
        try {
            restaurantOptional = Optional.ofNullable(
                    jdbcTemplate.queryForObject(sqlQuery, new RestaurantRowMapper(), id));
        } catch (EmptyResultDataAccessException emptyException) {
            restaurantOptional = Optional.empty();
        }
        return restaurantOptional;
    }

    public List<Restaurant> getRestaurants(Integer limit, Integer offset) {
        String sqlQuery = "SELECT * FROM restaurant LIMIT ? OFFSET ?";
        List<Restaurant> restaurantList = jdbcTemplate.query(sqlQuery, new RestaurantRowMapper(), limit, offset);
        return restaurantList;
    }

    public Restaurant addOrUpdateProduct(ProductRestaurant productRestaurant) {
        String sqlQuery = "INSERT INTO product_restaurant (product_id, restaurant_id, count) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (product_id, restaurant_id) DO UPDATE " +
                "SET count = product_restaurant.count + ?";
        Long productId = productRestaurant.getProduct().getId();
        Long restaurantId = productRestaurant.getRestaurant().getId();
        try {
            jdbcTemplate.update(sqlQuery, productId, restaurantId,
                    productRestaurant.getCount(), productRestaurant.getCount());
        } catch (DataAccessException exception) {
            throw new RestaurantAddProductFailedException();
        }
        Optional<Restaurant> restaurantOptional = getRestaurantWithProducts(productRestaurant.getRestaurant().getId());
        return restaurantOptional.orElseThrow(RestaurantNotFoundException::new);
    }

    public Optional<Restaurant> getRestaurantWithProducts(Long restaurantId) {
        String sqlQuery = "SELECT r.id AS restaurant_id, r.name AS restaurant_name, address AS restaurant_address, " +
                "r.description AS restaurant_description, p.id AS product_id, " +
                "p.name AS product_name, price AS product_price, " +
                "weight AS product_weight, p.description AS product_description, " +
                "category AS product_category, " +
                "count AS product_count FROM (SELECT * FROM restaurant AS r WHERE id = ?) AS r " +
                "JOIN product_restaurant AS pr ON r.id = pr.restaurant_id " +
                "JOIN public.product p on p.id = pr.product_id";
        Optional<Restaurant> restaurantOptional;
        try {
            restaurantOptional = jdbcTemplate.query(sqlQuery, new RestaurantResultSetExtractor(), restaurantId)
                    .stream().map(Optional::ofNullable)
                    .findFirst().orElse(Optional.empty());
        } catch (EmptyResultDataAccessException exception) {
            restaurantOptional = Optional.empty();
        }
        return restaurantOptional;
    }

    public Restaurant save(Restaurant restaurant) {
        String sqlQuery = "INSERT INTO restaurant(name, address, description) VALUES(?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[] {"id"});
                ps.setString(1, restaurant.getName());
                ps.setString(2, restaurant.getAddress());
                ps.setString(3, restaurant.getDescription());
                return ps;
            }, keyHolder);
            restaurant.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        } catch (DataAccessException exception) {
            throw new RestaurantCreationFailedException();
        }
        return restaurant;
    }

    public Restaurant update(Restaurant restaurant) {
        String sqlQuery = "UPDATE restaurant SET name=?, address=?, description=? WHERE id=?";
        Boolean isUpdated = false;
        try {
            isUpdated = (jdbcTemplate.update(sqlQuery, restaurant.getName(), restaurant.getAddress(),
                    restaurant.getDescription(), restaurant.getId())) != 0;
        } catch (DataAccessException exception) {
            throw new RestaurantUpdateFailedException();
        }
        if (!isUpdated) {
            throw new RestaurantNotFoundException();
        }
        return restaurant;
    }

    public Restaurant delete(Long id) {
        Restaurant restaurant = getRestaurant(id).orElseThrow(RestaurantNotFoundException::new);
        String sqlQuery = "DELETE FROM order_product WHERE order_id IN (SELECT id FROM \"order\" WHERE restaurant_id=?); " +
                "DELETE FROM \"order\" WHERE restaurant_id=?; " +
                "DELETE FROM product_restaurant WHERE restaurant_id=?; " +
                "DELETE FROM restaurant WHERE id=?";
        try {
            jdbcTemplate.update(sqlQuery, id, id, id, id);
        } catch (DataAccessException exception) {
            throw new RestaurantDeleteFailedException();
        }
        return restaurant;
    }

    public Boolean reduceProducts(Long restaurantId, Set<OrderProduct> orderProductSet) {
        return reduceProducts(restaurantId, new ArrayList<>(orderProductSet));
    }

    public Boolean reduceProducts(Long restaurantId, List<OrderProduct> orderProductList) {
        String sqlQuery = "UPDATE product_restaurant " +
                "SET count = product_restaurant.count - ? WHERE restaurant_id=? AND product_id=?";
        Boolean isReduced = Arrays.stream((jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, orderProductList.get(i).getCount());
                ps.setLong(2, restaurantId);
                ps.setLong(3, orderProductList.get(i).getProduct().getId());
            }
            @Override
            public int getBatchSize() {
                return orderProductList.size();
            }
        }))).sum() == orderProductList.size();
        return isReduced;
    }
}
