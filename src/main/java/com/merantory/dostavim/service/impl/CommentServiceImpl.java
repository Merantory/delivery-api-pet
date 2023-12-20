package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.exception.CommentAlreadyExistException;
import com.merantory.dostavim.exception.CommentNotFoundException;
import com.merantory.dostavim.exception.PersonNotFoundException;
import com.merantory.dostavim.exception.ProductNotFoundException;
import com.merantory.dostavim.model.Comment;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.repository.CommentRepository;
import com.merantory.dostavim.repository.PersonRepository;
import com.merantory.dostavim.repository.ProductRepository;
import com.merantory.dostavim.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final PersonRepository personRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, ProductRepository productRepository,
                              PersonRepository personRepository) {
        this.commentRepository = commentRepository;
        this.productRepository = productRepository;
        this.personRepository = personRepository;
    }

    @Override
    public Optional<Comment> getComment(Long id) {
        log.info("Trying to load comment with id: {}", id);
        Optional<Comment> commentOptional = commentRepository.getComment(id);
        return commentOptional;
    }

    @Override
    public List<Comment> getComments(Integer limit, Integer offset) {
        log.info("Trying to load comments with limit={} and offset={}", limit, offset);
        List<Comment> commentList = commentRepository.getComments(limit, offset);
        return commentList;
    }

    @Override
    public List<Comment> getProductComments(Long productId, Integer limit, Integer offset) {
        log.info("Trying to load comments for product with id={} with limit={} and offset={}", productId, limit, offset);
        if (!isExistProduct(productId)) {
            log.info("Product with id={} not found", productId);
            throw new ProductNotFoundException(String.format("Product with id %d not found.", productId));
        }
        List<Comment> commentList = commentRepository.getProductComments(productId, limit, offset);
        return commentList;
    }

    @Override
    public List<Comment> getPersonComments(Long personId, Integer limit, Integer offset) {
        log.info("Trying to load comments by person with id={} with limit={} and offset={}", personId, limit, offset);
        if (!isExistPerson(personId)) {
            log.info("Person with id={} not found", personId);
            throw new PersonNotFoundException(String.format("Person with id %d not found.", personId));
        }
        List<Comment> commentList = commentRepository.getPersonComments(personId, limit, offset);
        return commentList;
    }

    @Override
    @Transactional
    public Comment create(Comment comment) {
        log.info("Trying to create comment: {}", comment);
        if (!isExistProduct(comment.getProduct())) {
            log.info("Product with id={} not found", comment.getProduct().getId());
            throw new ProductNotFoundException(String.format("Product with id %d not found.",
                    comment.getProduct().getId()));
        }
        if (isExistCommentByPersonForThisProduct(comment.getCreator(), comment.getProduct())) {
            log.info("Comment by person with id={} for product with id={} already exist",
                    comment.getCreator().getId(), comment.getProduct().getId());
            throw new CommentAlreadyExistException(
                    String.format("Comment by person with id %d for product with id %d already exist.",
                            comment.getCreator().getId(), comment.getProduct().getId()));
        }
        comment = commentRepository.save(comment);
        log.info("Comment has been created: {}", comment);
        return comment;
    }

    @Override
    @Transactional
    public Comment delete(Long id) {
        log.info("Trying to delete comment with id={}", id);
        Optional<Comment> commentOptional = commentRepository.getComment(id);
        if (commentOptional.isEmpty()) {
            log.info("Comment with id={} not found", id);
            throw new CommentNotFoundException(String.format("Product with id %d not found.", id));
        }
        Boolean isDeleted = commentRepository.delete(id);
        log.info("Comment has been deleted with data={}", commentOptional.get());
        return commentOptional.get();
    }

    private Boolean isExistProduct(Product product) {
        return isExistProduct(product.getId());
    }

    private Boolean isExistProduct(Long productId) {
        Optional<Product> productOptional = productRepository.getProduct(productId);
        return productOptional.isPresent();
    }

    private Boolean isExistCommentByPersonForThisProduct(Person creator, Product product) {
        return isExistCommentByPersonForThisProduct(creator.getId(), product.getId());
    }

    private Boolean isExistCommentByPersonForThisProduct(Long creatorId, Long productId) {
        Optional<Comment> commentOptional = commentRepository.getPersonForProductComment(creatorId, productId);
        return commentOptional.isPresent();
    }

    private Boolean isExistPerson(Person person) {
        return isExistPerson(person.getId());
    }

    private Boolean isExistPerson(Long personId) {
        Optional<Person> personOptional = personRepository.getPerson(personId);
        return personOptional.isPresent();
    }
}
