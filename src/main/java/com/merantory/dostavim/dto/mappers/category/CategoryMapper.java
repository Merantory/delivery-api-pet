package com.merantory.dostavim.dto.mappers.category;

import com.merantory.dostavim.dto.impl.category.CategoryDto;
import com.merantory.dostavim.dto.impl.category.CreateCategoryDto;
import com.merantory.dostavim.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toCategory(CreateCategoryDto createCategoryDto) {
        Category category = new Category();
        category.setName(createCategoryDto.getName());
        return category;
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getName());
    }
}
