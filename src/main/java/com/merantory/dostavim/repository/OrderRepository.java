package com.merantory.dostavim.repository;

import com.merantory.dostavim.exception.OrderCreationFailedException;
import com.merantory.dostavim.model.Order;
import com.merantory.dostavim.model.OrderProduct;
import com.merantory.dostavim.repository.mappers.OrderResultSetExtractor;
import com.merantory.dostavim.repository.mappers.OrderRowMapper;
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
import java.sql.Timestamp;
import java.util.*;

@Repository
public class OrderRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Order> getOrder(Long id) {
        String sqlQuery = "SELECT " +
                "order_id, o.weight as order_weight, cost as order_cost, " +
                "order_date, order_status, restaurant_id, person_id, product_id, " +
                "count as product_count, p.weight as product_weight, price as product_price, " +
                "name as product_name, description as product_description, category as product_category " +
                "FROM (SELECT * FROM \"order\" WHERE id=?) as o " +
                "JOIN order_product op ON o.id = op.order_id " +
                "JOIN product p on p.id = op.product_id";
        Optional<Order> orderOptional;
        try {
           orderOptional = jdbcTemplate.query(sqlQuery, new OrderResultSetExtractor(), id)
                   .stream().map(Optional::ofNullable)
                   .findFirst().orElse(Optional.empty());
        } catch (EmptyResultDataAccessException emptyException) {
            orderOptional = Optional.empty();
        }
        return orderOptional;
    }

    public List<Order> getOrders(Integer limit, Integer offset) {
        String sqlQuery = "SELECT * FROM \"order\" LIMIT ? OFFSET ?";
        List<Order> orderList = jdbcTemplate.query(sqlQuery, new OrderRowMapper(), limit, offset);
        return orderList;
    }

    public List<Order> getDetailedOrders(Integer limit, Integer offset) {
        String sqlQuery = "SELECT " +
                "order_id, o.weight as order_weight, cost as order_cost, " +
                "order_date, order_status, restaurant_id, person_id, product_id, " +
                "count as product_count, p.weight as product_weight, price as product_price, " +
                "name as product_name, description as product_description, category as product_category " +
                "FROM (SELECT * FROM \"order\" LIMIT ? OFFSET ?) as o " +
                "JOIN order_product op ON o.id = op.order_id " +
                "JOIN product p on p.id = op.product_id";
        List<Order> orderList = jdbcTemplate.query(sqlQuery, new OrderResultSetExtractor(), limit, offset);
        return orderList;
    }

    public List<Order> getPersonOrders(Long ownerPersonId, Integer limit, Integer offset) {
        String sqlQuery = "SELECT * FROM \"order\" WHERE person_id=? LIMIT ? OFFSET ?";
        List<Order> orderList = jdbcTemplate.query(sqlQuery, new OrderRowMapper(), ownerPersonId, limit, offset);
        return orderList;
    }

    public List<Order> getPersonOrders(Long ownerPersonId, Integer limit, Integer offset, Boolean detailed) {
        if (!detailed) return getPersonOrders(ownerPersonId, limit, offset);
        String sqlQuery = "SELECT " +
                "order_id, o.weight as order_weight, cost as order_cost, " +
                "order_date, order_status, restaurant_id, person_id, product_id, " +
                "count as product_count, p.weight as product_weight, price as product_price, " +
                "name as product_name, description as product_description, category as product_category " +
                "FROM (SELECT * FROM \"order\" WHERE person_id=? LIMIT ? OFFSET ?) as o " +
                "JOIN order_product op ON o.id = op.order_id " +
                "JOIN product p on p.id = op.product_id";
        List<Order> orderList =
                jdbcTemplate.query(sqlQuery, new OrderResultSetExtractor(), ownerPersonId, limit, offset);
        return orderList;
    }

    public Order save(Order order) {
        try {
            saveOrder(order);
            saveOrderProducts(order);
            return getOrder(order.getId()).orElseThrow(OrderCreationFailedException::new);
        } catch (DataAccessException exception) {
            throw new OrderCreationFailedException();
        }
    }

    private Boolean saveOrder(Order order) {
        String sqlQuery = "INSERT INTO \"order\"(weight, cost, restaurant_id," +
                " person_id, order_status, order_date) VALUES(?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Boolean isSaved = (jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[] {"id"});
            ps.setDouble(1, order.getWeight());
            ps.setDouble(2, order.getCost());
            ps.setLong(3, order.getRestaurant().getId());
            ps.setLong(4, order.getPerson().getId());
            ps.setString(5, order.getOrderStatus());
            ps.setTimestamp(6, Timestamp.from(order.getOrderDate()), Calendar.getInstance(TimeZone.getTimeZone("UTC")));
            return ps;
        }, keyHolder)) != 0;
        Long orderId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        order.setId(orderId);
        return isSaved;
    }

    private Boolean saveOrderProducts(Order order) {
        return saveOrderProducts(order.getId(), new ArrayList<>(order.getOrderProductSet()));
    }

    private Boolean saveOrderProducts(Long orderId, List<OrderProduct> orderProductList) {
        String sqlQuery = "INSERT INTO order_product(order_id, product_id, count) VALUES (?, ?, ?)";
        Boolean isSaved = Arrays.stream((jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, orderId);
                ps.setLong(2, orderProductList.get(i).getProduct().getId());
                ps.setInt(3, orderProductList.get(i).getCount());
            }
            @Override
            public int getBatchSize() {
                return orderProductList.size();
            }
        }))).sum() != 0;
        return isSaved;
    }
}
