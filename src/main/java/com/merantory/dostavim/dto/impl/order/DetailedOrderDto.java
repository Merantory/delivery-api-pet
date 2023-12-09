package com.merantory.dostavim.dto.impl.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.merantory.dostavim.dto.impl.orderProduct.OrderProductDto;
import com.merantory.dostavim.dto.markerInterfaces.Views;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonView(Views.Public.class)
public class DetailedOrderDto extends OrderDto {
    @JsonProperty("products")
    private Set<OrderProductDto> orderProductDtoSet;
}
