package com.merantory.dostavim.repository;

import com.merantory.dostavim.model.OrderProduct;
import com.merantory.dostavim.model.ProductRestaurant;
import com.merantory.dostavim.model.Restaurant;
import com.merantory.dostavim.repository.mappers.RestaurantRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
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

    public Boolean addOrUpdateProduct(ProductRestaurant productRestaurant) {
        String sqlQuery = "INSERT INTO product_restaurant (product_id, restaurant_id, count) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (product_id, restaurant_id) DO UPDATE " +
                "SET count = product_restaurant.count + ?";
        Long productId = productRestaurant.getProduct().getId();
        Long restaurantId = productRestaurant.getRestaurant().getId();
        Boolean isApplied = (jdbcTemplate.update(sqlQuery, productId, restaurantId,
                productRestaurant.getCount(), productRestaurant.getCount()) != 0);
        return isApplied;
    }

    public Boolean save(Restaurant restaurant) {
        String sqlQuery = "INSERT INTO restaurant(name, address, description) VALUES(?,?,?)";
        Boolean isSaved = false;
        isSaved = (jdbcTemplate.update(sqlQuery,
                restaurant.getName(), restaurant.getAddress(), restaurant.getDescription())) != 0;
        return isSaved;
    }

    public Boolean update(Restaurant restaurant) {
        String sqlQuery = "UPDATE restaurant SET name=?, address=?, description=? WHERE id=?";
        Boolean isUpdated = (jdbcTemplate.update(sqlQuery, restaurant.getName(), restaurant.getAddress(),
                restaurant.getDescription(), restaurant.getId())) != 0;
        return isUpdated;
    }

    public Boolean delete(Long id) {
        String sqlQuery = "DELETE FROM restaurant WHERE id=?";
        Boolean isDeleted = (jdbcTemplate.update(sqlQuery, id)) != 0;
        return isDeleted;
    }

    public boolean reduceProducts(Long restaurantId, Set<OrderProduct> orderProductSet) {
        return reduceProducts(restaurantId, new ArrayList<>(orderProductSet));
    }

    public boolean reduceProducts(Long restaurantId, List<OrderProduct> orderProductList) {
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
