package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.exception.CommentAlreadyExistException;
import com.merantory.dostavim.exception.PersonNotFoundException;
import com.merantory.dostavim.exception.ProductNotFoundException;
import com.merantory.dostavim.model.Comment;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.repository.CommentRepository;
import com.merantory.dostavim.repository.PersonRepository;
import com.merantory.dostavim.repository.ProductRepository;
import com.merantory.dostavim.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
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
        Optional<Comment> commentOptional = commentRepository.getComment(id);
        return commentOptional;
    }

    @Override
    public List<Comment> getComments(Integer limit, Integer offset) {
        List<Comment> commentList = commentRepository.getComments(limit, offset);
        return commentList;
    }

    @Override
    public List<Comment> getProductComments(Long productId, Integer limit, Integer offset) {
        if (!isExistProduct(productId)) {
            throw new ProductNotFoundException();
        }
        List<Comment> commentList = commentRepository.getProductComments(productId, limit, offset);
        return commentList;
    }

    @Override
    public List<Comment> getPersonComments(Long personId, Integer limit, Integer offset) {
        if (!isExistPerson(personId)) {
            throw new PersonNotFoundException();
        }
        List<Comment> commentList = commentRepository.getPersonComments(personId, limit, offset);
        return commentList;
    }

    @Override
    @Transactional
    public Comment create(Comment comment) {
        if (!isExistProduct(comment.getProduct())) {
            throw new ProductNotFoundException();
        }
        if (isExistCommentByPersonForThisProduct(comment.getCreator(), comment.getProduct())) {
            throw new CommentAlreadyExistException();
        }
        comment = commentRepository.save(comment);
        return comment;
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
