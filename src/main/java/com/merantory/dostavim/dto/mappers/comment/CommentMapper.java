package com.merantory.dostavim.dto.mappers.comment;

import com.merantory.dostavim.dto.impl.comment.CommentDto;
import com.merantory.dostavim.dto.impl.comment.CreateCommentDto;
import com.merantory.dostavim.model.Comment;
import com.merantory.dostavim.model.Product;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public Comment toComment(CreateCommentDto createCommentDto) {
        Comment comment = new Comment();
        comment.setContent(createCommentDto.getContent());

        Product product = new Product();
        product.setId(createCommentDto.getProductId());
        comment.setProduct(product);

        return comment;
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setProductId(comment.getProduct().getId());
        commentDto.setCreatorId(comment.getCreator().getId());
        commentDto.setContent(comment.getContent());

        return commentDto;
    }
}
