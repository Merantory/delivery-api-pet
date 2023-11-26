package com.merantory.dostavim.repository;

import com.merantory.dostavim.model.Category;
import com.merantory.dostavim.repository.mappers.CategoryRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void save(Category category) {
        String sqlQuery = "INSERT INTO category(name) VALUES(?)";
        jdbcTemplate.update(sqlQuery, category.getName());
    }
}
