package com.merantory.dostavim.dto.mappers.orderProduct;

import com.merantory.dostavim.dto.impl.orderProduct.CreateOrderProductDto;
import com.merantory.dostavim.dto.impl.orderProduct.OrderProductDto;
import com.merantory.dostavim.dto.mappers.order.OrderMapper;
import com.merantory.dostavim.dto.mappers.product.ProductMapper;
import com.merantory.dostavim.model.OrderProduct;
import com.merantory.dostavim.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class OrderProductMapper {
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderProductMapper(@Lazy ProductMapper productMapper, @Lazy OrderMapper orderMapper) {
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
    }

    public OrderProduct toOrderProduct(CreateOrderProductDto createOrderProductDto) {
        OrderProduct orderProduct = new OrderProduct();
        Product product = new Product();
        product.setId(createOrderProductDto.getProductId());
        orderProduct.setProduct(product);
        orderProduct.setCount(createOrderProductDto.getCount());

        return orderProduct;
    }

    public Set<OrderProduct> toOrderProductSet(Set<CreateOrderProductDto> createOrderProductDtoSet) {
        Set<OrderProduct> orderProductSet = new HashSet<>();
        for (CreateOrderProductDto createOrderProductDto : createOrderProductDtoSet) {
            orderProductSet.add(toOrderProduct(createOrderProductDto));
        }
        return orderProductSet;
    }

    public OrderProductDto toOrderProductDto(OrderProduct orderProduct) {
        OrderProductDto orderProductDto = new OrderProductDto();
        if (orderProduct.getOrder() != null) {
            orderProductDto.setOrderDto(orderMapper.toOrderDto(orderProduct.getOrder()));
        }
        if (orderProduct.getProduct() != null) {
            orderProductDto.setProductDto(productMapper.toProductDto(orderProduct.getProduct()));
        }
        orderProductDto.setCount(orderProduct.getCount());
        return orderProductDto;
    }

    public Set<OrderProductDto> toOrderProductDtoSet(Set<OrderProduct> orderProductSet) {
        Set<OrderProductDto> orderProductDtoSet = new HashSet<>();
        for (OrderProduct orderProduct : orderProductSet) {
            orderProductDtoSet.add(toOrderProductDto(orderProduct));
        }
        return orderProductDtoSet;
    }
}
