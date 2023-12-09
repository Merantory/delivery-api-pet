package com.merantory.dostavim.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.merantory.dostavim.dto.impl.order.CreateOrderDto;
import com.merantory.dostavim.dto.mappers.order.OrderMapper;
import com.merantory.dostavim.dto.markerInterfaces.Views;
import com.merantory.dostavim.exception.IllegalLimitArgumentException;
import com.merantory.dostavim.exception.IllegalOffsetArgumentException;
import com.merantory.dostavim.exception.OrderNotFoundException;
import com.merantory.dostavim.exception.PersonAuthFailedException;
import com.merantory.dostavim.model.Order;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        Optional<Order> orderOptional = orderService.getOrder(id);
        if (orderOptional.isEmpty()) throw new OrderNotFoundException();
        return new ResponseEntity<>(orderMapper.toOrderDto(orderOptional.get()), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getOrders(@RequestParam(value = "limit") Optional<Integer> limitOptional,
                                       @RequestParam(value = "offset") Optional<Integer> offsetOptional,
                                       @RequestParam(value = "detailed") Optional<Boolean> detailedOptional) {
        Integer limit = limitOptional.orElse(1);
        Integer offset = offsetOptional.orElse(0);
        Boolean detailed = detailedOptional.orElse(false);

        if (limit < 1) throw new IllegalLimitArgumentException();
        if (offset < 0) throw new IllegalOffsetArgumentException();

        return new ResponseEntity<>(orderService.getOrders(limit, offset, detailed).stream()
                .map((detailed) ? orderMapper::toDetailedOrderDto : orderMapper::toOrderDto).toList(), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<?> getPersonOrders(@RequestParam(value = "limit") Optional<Integer> limitOptional,
                                       @RequestParam(value = "offset") Optional<Integer> offsetOptional,
                                       @RequestParam(value = "detailed") Optional<Boolean> detailedOptional) {
        Integer limit = limitOptional.orElse(1);
        Integer offset = offsetOptional.orElse(0);
        Boolean detailed = detailedOptional.orElse(true);

        if (limit < 1) throw new IllegalLimitArgumentException();
        if (offset < 0) throw new IllegalOffsetArgumentException();
        Long ordersOwnerId = getAuthenticationPerson().getId();

        return new ResponseEntity<>(orderService.getPersonOrders(ordersOwnerId, limit, offset, detailed).stream()
                .map((detailed) ? orderMapper::toDetailedOrderDto : orderMapper::toOrderDto).toList(), HttpStatus.OK);
    }

    @PostMapping
    @JsonView(Views.Public.class)
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderDto createOrderDto) {
        Person creator = getAuthenticationPerson();
        Order order = orderMapper.toOrder(createOrderDto);
        order.setPerson(creator);
        Order createdOrder = orderService.create(order);
        return new ResponseEntity<>(orderMapper.toDetailedOrderDto(createdOrder), HttpStatus.CREATED);
    }

    private Person getAuthenticationPerson() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return (Person) authentication.getPrincipal();
        } else {
            throw new PersonAuthFailedException("Person authentication failed");
        }
    }
}
