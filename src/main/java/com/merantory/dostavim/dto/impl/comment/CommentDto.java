package com.merantory.dostavim.dto.impl.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("creator_id")
    private Long creatorId;
    private String content;
}
