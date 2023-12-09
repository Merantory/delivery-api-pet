package com.merantory.dostavim.dto.impl.orderProduct;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.merantory.dostavim.dto.impl.order.OrderDto;
import com.merantory.dostavim.dto.impl.product.ProductDto;
import com.merantory.dostavim.dto.markerInterfaces.Views;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonView(Views.Public.class)
public class OrderProductDto {
    @JsonProperty("order")
    private OrderDto orderDto;
    @JsonProperty("product")
    private ProductDto productDto;
    private Integer count;
}
