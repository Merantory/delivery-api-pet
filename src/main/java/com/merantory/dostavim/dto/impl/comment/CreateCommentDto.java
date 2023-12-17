package com.merantory.dostavim.dto.impl.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentDto {
    @JsonProperty("product_id")
    @NotNull(message = "Значение поля не должно быть пустым")
    @Positive(message = "Значение поля должно быть положительным")
    private Long productId;

    @NotEmpty(message = "Значение поля не должно быть пустым")
    private String content;
}
