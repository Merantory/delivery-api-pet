package com.merantory.dostavim.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Comment {
    private Long id;
    private Product product;
    private Person creator;
    private String content;
}
