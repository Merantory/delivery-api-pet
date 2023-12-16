package com.merantory.dostavim.repository;

import com.merantory.dostavim.exception.CategoryCreationFailedException;
import com.merantory.dostavim.model.Category;
import com.merantory.dostavim.repository.mappers.CategoryRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Category> getCategories(Integer limit, Integer offset) {
        String sqlQuery = "SELECT * FROM category LIMIT ? OFFSET ?";
        List<Category> categoryList = jdbcTemplate.query(sqlQuery, new CategoryRowMapper(), limit, offset);
        return categoryList;
    }

    public Boolean isExistCategory(Category category) {
        String sqlQuery = "SELECT COUNT(*) FROM category WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, category.getName());
        return count > 0;
    }

    public Category save(Category category) {
        String sqlQuery = "INSERT INTO category(name) VALUES(?)";
        try {
            jdbcTemplate.update(sqlQuery, category.getName());
        } catch (DataAccessException exception) {
            throw new CategoryCreationFailedException();
        }
        return category;
    }
}
