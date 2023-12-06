package com.merantory.dostavim.repository.mappers;

import com.merantory.dostavim.model.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class OrderResultSetExtractor implements ResultSetExtractor<List<Order>> {
    @Override
    public List<Order> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Order> idOrderMap = new HashMap<>();
        while (rs.next()) {
            Long orderId = rs.getLong("order_id");
            if (!idOrderMap.containsKey(orderId)) {
                Order order = new Order();
                order.setId(orderId);
                order.setWeight(rs.getDouble("order_weight"));
                order.setCost(rs.getDouble("order_cost"));
                order.setOrderDate(rs.getTimestamp("order_date",
                                Calendar.getInstance(TimeZone.getTimeZone("UTC")))
                        .toInstant());
                order.setOrderStatus(rs.getString("order_status"));

                Person person = new Person();
                person.setId(rs.getLong("person_id"));
                order.setPerson(person);

                Restaurant restaurant = new Restaurant();
                restaurant.setId(rs.getLong("restaurant_id"));
                order.setRestaurant(restaurant);

                idOrderMap.put(orderId, order);
            }
            Product product = new Product();
            product.setId(rs.getLong("product_id"));
            product.setWeight(rs.getDouble("product_weight"));
            product.setPrice(rs.getDouble("product_price"));
            product.setName(rs.getString("product_name"));
            product.setDescription(rs.getString("product_description"));
            Category category = new Category();
            category.setName(rs.getString("product_category"));
            product.setCategory(category);

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProduct(product);
            orderProduct.setCount(rs.getInt("product_count"));
            if (idOrderMap.get(orderId).getOrderProductSet() == null) {
                idOrderMap.get(orderId).setOrderProductSet(new HashSet<>());
            }
            idOrderMap.get(orderId).getOrderProductSet().add(orderProduct);
        }
        return new ArrayList<>(idOrderMap.values());
    }
}
