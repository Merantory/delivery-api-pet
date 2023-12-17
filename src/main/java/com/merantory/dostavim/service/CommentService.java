package com.merantory.dostavim.service;

import com.merantory.dostavim.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<Comment> getComment(Long id);
    List<Comment> getComments(Integer limit, Integer offset);
    List<Comment> getProductComments(Long productId, Integer limit, Integer offset);
    List<Comment> getPersonComments(Long personId, Integer limit, Integer offset);
    Comment create(Comment comment);
}
