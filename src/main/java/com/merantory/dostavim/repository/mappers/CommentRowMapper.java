package com.merantory.dostavim.repository.mappers;

import com.merantory.dostavim.model.Comment;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.model.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommentRowMapper implements RowMapper<Comment> {
    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("comment_id"));
        comment.setContent(rs.getString("content"));

        Product product = new Product();
        product.setId(rs.getLong("product_id"));

        Person person = new Person();
        person.setId(rs.getLong("person_id"));

        comment.setProduct(product);
        comment.setCreator(person);

        return comment;
    }
}
