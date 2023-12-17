package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.comment.CommentDto;
import com.merantory.dostavim.dto.impl.comment.CreateCommentDto;
import com.merantory.dostavim.dto.mappers.comment.CommentMapper;
import com.merantory.dostavim.exception.CommentNotFoundException;
import com.merantory.dostavim.exception.IllegalLimitArgumentException;
import com.merantory.dostavim.exception.IllegalOffsetArgumentException;
import com.merantory.dostavim.exception.PersonAuthFailedException;
import com.merantory.dostavim.model.Comment;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tags(
        value = {
                @Tag(name = "comment-controller", description = "API для работы с комментариями товаров")
        }
)
@RestController
@RequestMapping("/comments")
@Validated
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @Autowired
    public CommentController(CommentService commentService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable("id") @Positive Long id) {
        Optional<Comment> commentOptional = commentService.getComment(id);
        if (commentOptional.isEmpty()) {
            throw new CommentNotFoundException();
        }
        return new ResponseEntity<>(commentMapper.toCommentDto(commentOptional.get()), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@RequestParam(value = "limit") Optional<Integer> limitOptional,
                                                        @RequestParam(value = "offset") Optional<Integer> offsetOptional) {
        Integer limit = limitOptional.orElse(1);
        Integer offset = offsetOptional.orElse(0);

        if (limit < 1) throw new IllegalLimitArgumentException();
        if (offset < 0) throw new IllegalOffsetArgumentException();

        return new ResponseEntity<>(commentService.getComments(limit, offset).stream().map(commentMapper::toCommentDto).toList(), HttpStatus.OK);
    }

    @GetMapping("/products/{product_id}")
    public ResponseEntity<List<CommentDto>> getProductComments(@PathVariable("product_id") @Positive Long productId,
                                                               @RequestParam(value = "limit") Optional<Integer> limitOptional,
                                                               @RequestParam(value = "offset") Optional<Integer> offsetOptional) {
        Integer limit = limitOptional.orElse(1);
        Integer offset = offsetOptional.orElse(0);

        if (limit < 1) throw new IllegalLimitArgumentException();
        if (offset < 0) throw new IllegalOffsetArgumentException();

        return new ResponseEntity<>(commentService.getProductComments(productId, limit, offset).stream().map(commentMapper::toCommentDto).toList(), HttpStatus.OK);
    }

    @GetMapping("/users/{person_id}")
    public ResponseEntity<List<CommentDto>> getPersonComments(@PathVariable("person_id") @Positive Long personId,
                                                               @RequestParam(value = "limit") Optional<Integer> limitOptional,
                                                               @RequestParam(value = "offset") Optional<Integer> offsetOptional) {
        Integer limit = limitOptional.orElse(1);
        Integer offset = offsetOptional.orElse(0);

        if (limit < 1) throw new IllegalLimitArgumentException();
        if (offset < 0) throw new IllegalOffsetArgumentException();

        return new ResponseEntity<>(commentService.getPersonComments(personId, limit, offset).stream().map(commentMapper::toCommentDto).toList(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CreateCommentDto createCommentDto) {
        Comment comment = commentMapper.toComment(createCommentDto);
        Person creator = getAuthenticationPerson();
        comment.setCreator(creator);
        comment = commentService.create(comment);
        return new ResponseEntity<>(commentMapper.toCommentDto(comment), HttpStatus.CREATED);
    }

    private Person getAuthenticationPerson() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return (Person) authentication.getPrincipal();
        } else {
            throw new PersonAuthFailedException("Person authentication failed");
        }
    }
}
