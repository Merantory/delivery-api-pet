package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.exception.OrderCreationFailedException;
import com.merantory.dostavim.exception.PersonNotFoundException;
import com.merantory.dostavim.exception.RestaurantNotFoundException;
import com.merantory.dostavim.model.*;
import com.merantory.dostavim.repository.OrderRepository;
import com.merantory.dostavim.repository.PersonRepository;
import com.merantory.dostavim.repository.ProductRepository;
import com.merantory.dostavim.repository.RestaurantRepository;
import com.merantory.dostavim.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final PersonRepository personRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, RestaurantRepository restaurantRepository,
                            ProductRepository productRepository, PersonRepository personRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
        this.personRepository = personRepository;
    }

    @Override
    public Optional<Order> getOrder(Long id) {
        log.info("Trying to load order with id: {}", id);
        Optional<Order> orderOptional = orderRepository.getOrder(id);
        return orderOptional;
    }

    @Override
    public List<Order> getOrders(Integer limit, Integer offset, Boolean detailed) {
        log.info("Trying to load orders with limit={} and offset={} and detailed={}", limit, offset, detailed);
        List<Order> orderList = (detailed) ? orderRepository.getDetailedOrders(limit, offset)
                : orderRepository.getOrders(limit, offset);
        return orderList;
    }

    @Override
    public List<Order> getPersonOrders(Long ownerPersonId, Integer limit, Integer offset, Boolean detailed) {
        log.info("Trying to load orders by person with id={} with limit={} and offset={} and detailed={}",
                ownerPersonId, limit, offset, detailed);
        if (!isExistPerson(ownerPersonId)) {
            log.info("Person with id={} not found", ownerPersonId);
            throw new PersonNotFoundException(String.format("Person with id %d not found.", ownerPersonId));
        }
        return orderRepository.getPersonOrders(ownerPersonId, limit, offset, detailed);
    }

    @Override
    @Transactional
    public Order create(Order order) {
        log.info("Trying to create order: {}", order);
        if (order.getPerson().getAddress() == null || order.getPerson().getAddress().isBlank()) {
            log.info("Failed to create order for person. Person address not received. Person data={}",
                    order.getPerson());
            throw new OrderCreationFailedException(
                    String.format("Before creating order, you should specify address for Person with id %d.",
                            order.getPerson().getId()));
        }
        if (!isExistRestaurant(order.getRestaurant())) {
            log.info("Restaurant with id={} not found", order.getRestaurant().getId());
            throw new RestaurantNotFoundException(String.format("Restaurant with id %d not found.",
                    order.getRestaurant().getId()));
        }
        Map<Long, Integer> productIdsCount = order.getOrderProductSet().stream()
                .collect(Collectors.toMap(orderProduct -> orderProduct.getProduct().getId(), OrderProduct::getCount));
        List<Product> productList = productRepository.getProductsByIds(productIdsCount.keySet());

        BigDecimal orderTotalWeight = BigDecimal.ZERO;
        BigDecimal orderTotalCost = BigDecimal.ZERO;
        for (Product product : productList) {
            BigDecimal productWeight = BigDecimal.valueOf(product.getWeight());
            BigDecimal productPrice = BigDecimal.valueOf(product.getPrice());
            BigDecimal count = BigDecimal.valueOf(productIdsCount.get(product.getId()));
            orderTotalWeight = orderTotalWeight.add(productWeight.multiply(count));
            orderTotalCost = orderTotalCost.add(productPrice.multiply(count));
        }
        order.setWeight(orderTotalWeight.doubleValue());
        order.setCost(orderTotalCost.doubleValue());
        order.setOrderDate(Instant.now());
        order.setOrderStatus("In process");
        log.info("Prepared for save order: {}", order);
        try {
            orderRepository.save(order);
            restaurantRepository.reduceProducts(order.getRestaurant().getId(), order.getOrderProductSet());
            log.info("Order has been created: {}", order);
            return orderRepository.getOrder(order.getId()).orElseThrow(OrderCreationFailedException::new);
        } catch (DataAccessException exception) {
            throw new OrderCreationFailedException();
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

    private Boolean isExistPerson(Person person) {
        return isExistPerson(person.getId());
    }

    private Boolean isExistPerson(Long personId) {
        Optional<Person> personOptional = personRepository.getPerson(personId);
        return personOptional.isPresent();
    }

    private Boolean isExistRestaurant(Restaurant restaurant) {
        return isExistRestaurant(restaurant.getId());
    }

    private Boolean isExistRestaurant(Long restaurantId) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.getRestaurant(restaurantId);
        return restaurantOptional.isPresent();
    }
}
