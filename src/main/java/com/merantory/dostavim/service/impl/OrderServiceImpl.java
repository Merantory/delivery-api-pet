package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.model.Order;
import com.merantory.dostavim.model.OrderProduct;
import com.merantory.dostavim.repository.OrderRepository;
import com.merantory.dostavim.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
    public Boolean create(Order order) {
        Double weight = 0D;
        Double cost = 0D;
        for (OrderProduct orderProduct : order.getOrderProductSet()) {
            weight += orderProduct.getProduct().getWeight() * orderProduct.getCount();
            cost += orderProduct.getProduct().getPrice() * orderProduct.getCount();
        }
        order.setWeight(weight);
        order.setCost(cost);
        order.setOrderDate(Instant.now());
        order.setOrderStatus("In process");
        return orderRepository.save(order);
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
