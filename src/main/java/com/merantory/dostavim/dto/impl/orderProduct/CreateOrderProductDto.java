package com.merantory.dostavim.dto.impl.orderProduct;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderProductDto {
    @JsonProperty("product_id")
    @NotNull(message = "Значение поля не должно быть пустым")
    @Positive(message = "Значение поля должно быть положительным")
    private Long productId;

    @NotNull(message = "Значение поля не должно быть пустым")
    @Positive(message = "Значение поля должно быть положительным")
    private Integer count;
}
