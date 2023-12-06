package com.merantory.dostavim.dto.mappers.order;

import com.merantory.dostavim.dto.impl.order.CreateOrderDto;
import com.merantory.dostavim.dto.impl.order.DetailedOrderDto;
import com.merantory.dostavim.dto.impl.order.OrderDto;
import com.merantory.dostavim.dto.mappers.orderProduct.OrderProductMapper;
import com.merantory.dostavim.model.Order;
import com.merantory.dostavim.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    private final OrderProductMapper orderProductMapper;

    @Autowired
    public OrderMapper(OrderProductMapper orderProductMapper) {
        this.orderProductMapper = orderProductMapper;
    }

    public OrderDto toOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        mapCommonProperties(order, orderDto);
        return orderDto;
    }

    public Order toOrder(CreateOrderDto createOrderDto) {
        Order order = new Order();
        Restaurant restaurant = new Restaurant();
        restaurant.setId(createOrderDto.getRestaurantId());
        order.setRestaurant(restaurant);
        order.setOrderProductSet(orderProductMapper.toOrderProductSet(createOrderDto.getCreateOrderProductDtoSet()));
        return order;
    }

    public DetailedOrderDto toDetailedOrderDto(Order order) {
        DetailedOrderDto detailedOrderDto = new DetailedOrderDto();
        mapCommonProperties(order, detailedOrderDto);
        detailedOrderDto.setOrderProductDtoSet(orderProductMapper.toOrderProductDtoSet(order.getOrderProductSet()));
        return detailedOrderDto;
    }

    private void mapCommonProperties(Order order, OrderDto orderDto) {
        orderDto.setId(order.getId());
        orderDto.setWeight(order.getWeight());
        orderDto.setCost(order.getCost());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setOrderStatus(order.getOrderStatus());
        orderDto.setRestaurantId(order.getRestaurant().getId());
        orderDto.setPersonId(order.getPerson().getId());
    }
}
