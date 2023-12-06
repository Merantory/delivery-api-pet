package com.merantory.dostavim.repository.mappers;

import com.merantory.dostavim.model.Order;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.model.Restaurant;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;

public class OrderRowMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setWeight(rs.getDouble("weight"));
        order.setCost(rs.getDouble("cost"));
        order.setOrderDate(rs.getTimestamp("order_date",
                Calendar.getInstance(TimeZone.getTimeZone("UTC")))
                .toInstant());
        order.setOrderStatus(rs.getString("order_status"));

        Person orderCreator = new Person();
        orderCreator.setId(rs.getLong("person_id"));
        order.setPerson(orderCreator);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(rs.getLong("restaurant_id"));
        order.setRestaurant(restaurant);

        return order;
    }
}
