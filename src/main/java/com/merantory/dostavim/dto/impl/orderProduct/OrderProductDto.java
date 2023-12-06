package com.merantory.dostavim.dto.impl.orderProduct;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.merantory.dostavim.dto.impl.order.OrderDto;
import com.merantory.dostavim.dto.impl.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderProductDto {
    @JsonProperty("order")
    private OrderDto orderDto;
    @JsonProperty("product")
    private ProductDto productDto;
    private Integer count;
}
