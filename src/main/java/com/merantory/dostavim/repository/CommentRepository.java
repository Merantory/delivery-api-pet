package com.merantory.dostavim.repository;

import com.merantory.dostavim.exception.CommentCreationFailedException;
import com.merantory.dostavim.model.Comment;
import com.merantory.dostavim.repository.mappers.CommentRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class CommentRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Comment> getComment(Long id) {
        String sqlQuery = "SELECT * FROM comment WHERE comment_id=?";
        Optional<Comment> commentOptional;
        try {
            commentOptional = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, new CommentRowMapper(), id));
        } catch (EmptyResultDataAccessException emptyException) {
            commentOptional = Optional.empty();
        }
        return commentOptional;
    }

    public List<Comment> getComments(Integer limit, Integer offset) {
        String sqlQuery = "SELECT * FROM comment LIMIT ? OFFSET ?";
        List<Comment> commentList = jdbcTemplate.query(sqlQuery, new CommentRowMapper(), limit, offset);
        return commentList;
    }


    public List<Comment> getProductComments(Long productId, Integer limit, Integer offset) {
        String sqlQuery = "SELECT * FROM comment WHERE product_id=? LIMIT ? OFFSET ?";
        List<Comment> commentList = jdbcTemplate.query(sqlQuery, new CommentRowMapper(), productId, limit, offset);
        return commentList;
    }

    public List<Comment> getPersonComments(Long personId, Integer limit, Integer offset) {
        String sqlQuery = "SELECT * FROM comment WHERE person_id=? LIMIT ? OFFSET ?";
        List<Comment> commentList = jdbcTemplate.query(sqlQuery, new CommentRowMapper(), personId, limit, offset);
        return commentList;
    }

    public Optional<Comment> getPersonForProductComment(Long creatorId, Long productId) {
        String sqlQuery = "SELECT * FROM comment WHERE person_id=? AND product_id = ?";
        Optional<Comment> commentOptional;
        try {
            commentOptional = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, new CommentRowMapper(), creatorId, productId));
        } catch (EmptyResultDataAccessException emptyException) {
            commentOptional = Optional.empty();
        }
        return commentOptional;
    }

    public Comment save(Comment comment) {
        try {
            String sqlQuery = "INSERT INTO comment(product_id, person_id, content) VALUES(?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            Boolean isSaved = (jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"comment_id"});
                ps.setLong(1, comment.getProduct().getId());
                ps.setLong(2, comment.getCreator().getId());
                ps.setString(3, comment.getContent());
                return ps;
            }, keyHolder)) != 0;
            Long commentId = Objects.requireNonNull(keyHolder.getKey()).longValue();
            comment.setId(commentId);
            return comment;
        } catch (DataAccessException exception) {
            throw new CommentCreationFailedException();
        }
    }
}
