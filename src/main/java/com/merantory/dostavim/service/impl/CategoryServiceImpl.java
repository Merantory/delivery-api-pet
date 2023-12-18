package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.exception.CategoryAlreadyExistsException;
import com.merantory.dostavim.model.Category;
import com.merantory.dostavim.repository.CategoryRepository;
import com.merantory.dostavim.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getCategories(Integer limit, Integer offset) {
        log.info("Trying to load categories with limit={} and offset={}", limit, offset);
        return categoryRepository.getCategories(limit, offset);
    }

    @Override
    @Transactional
    public Category create(Category category) {
        if (isExistCategory(category)) {
            log.info("Already exist category: {}", category);
            throw new CategoryAlreadyExistsException(
                    String.format("Category with name '%s' already exists.", category.getName()));
        }
        log.info("Trying to create new category: {}", category);
        category = categoryRepository.save(category);
        log.info("Category has been created: {}", category);
        return category;
    }

    private Boolean isExistCategory(Category category) {
        return categoryRepository.isExistCategory(category);
    }
}