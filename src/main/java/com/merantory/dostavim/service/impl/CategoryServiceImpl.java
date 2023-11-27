package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.model.Category;
import com.merantory.dostavim.repository.CategoryRepository;
import com.merantory.dostavim.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getCategories(Integer limit, Integer offset) {
        return categoryRepository.getCategories(limit, offset);
    }

    @Override
    @Transactional
    public void create(Category category) {
        categoryRepository.save(category);
    }
}