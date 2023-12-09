package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.exception.OrderCreationFailed;
import com.merantory.dostavim.model.Order;
import com.merantory.dostavim.model.OrderProduct;
import com.merantory.dostavim.repository.OrderRepository;
import com.merantory.dostavim.repository.RestaurantRepository;
import com.merantory.dostavim.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Optional<Order> getOrder(Long id) {
        Optional<Order> orderOptional = orderRepository.getOrder(id);
        return orderOptional;
    }

    @Override
    public List<Order> getOrders(Integer limit, Integer offset, Boolean detailed) {
        return orderRepository.getOrders(limit, offset, detailed);
    }

    @Override
    public List<Order> getPersonOrders(Long ownerPersonId, Integer limit, Integer offset, Boolean detailed) {
        return orderRepository.getPersonOrders(ownerPersonId, limit, offset, detailed);
    }

    @Override
    @Transactional
    public Order create(Order order) {
        BigDecimal weight = BigDecimal.ZERO;
        BigDecimal cost = BigDecimal.ZERO;
        for (OrderProduct orderProduct : order.getOrderProductSet()) {
            BigDecimal productWeight = BigDecimal.valueOf(orderProduct.getProduct().getWeight());
            BigDecimal productPrice = BigDecimal.valueOf(orderProduct.getProduct().getPrice());
            BigDecimal count = BigDecimal.valueOf(orderProduct.getCount());
            weight = weight.add(productWeight.multiply(count));
            cost = cost.add(productPrice.multiply(count));
        }
        order.setWeight(weight.doubleValue());
        order.setCost(cost.doubleValue());
        order.setOrderDate(Instant.now());
        order.setOrderStatus("In process");
        try {
            orderRepository.save(order);
            restaurantRepository.reduceProducts(order.getRestaurant().getId(), order.getOrderProductSet());
            return order;
        } catch (DataAccessException exception) {
            throw new OrderCreationFailed();
        }
    }

    @Override
    @Transactional
    public Boolean update(Order order) {
        return null;
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        return null;
    }
}
