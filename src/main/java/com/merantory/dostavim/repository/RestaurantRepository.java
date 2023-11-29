package com.merantory.dostavim.repository;

import com.merantory.dostavim.model.Restaurant;
import com.merantory.dostavim.repository.mappers.RestaurantRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
}
