package com.merantory.dostavim.service;

import com.merantory.dostavim.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Optional<Order> getOrder(Long id);
    List<Order> getOrders(Integer limit, Integer offset, Boolean detailed);
    List<Order> getPersonOrders(Long ownerPersonId, Integer limit, Integer offset, Boolean detailed);
    Order create(Order order);
    Boolean update(Order order);
    Boolean delete(Long id);
}
