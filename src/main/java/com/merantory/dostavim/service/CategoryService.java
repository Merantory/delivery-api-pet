package com.merantory.dostavim.service;

import com.merantory.dostavim.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories(Integer limit, Integer offset);

    Category create(Category category);
}
