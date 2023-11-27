package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.category.CreateCategoryDto;
import com.merantory.dostavim.dto.mappers.category.CategoryMapper;
import com.merantory.dostavim.model.Category;
import com.merantory.dostavim.service.CategoryService;
import com.merantory.dostavim.exception.IllegalLimitArgumentException;
import com.merantory.dostavim.exception.IllegalOffsetArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    public ResponseEntity<?> getCategories(@RequestParam(value = "limit") Optional<Integer> limitOptional,
                              @RequestParam(value = "offset") Optional<Integer> offsetOptional) {
        Integer limit = limitOptional.orElse(1);
        Integer offset = offsetOptional.orElse(0);

        if (limit < 1) throw new IllegalLimitArgumentException();
        if (offset < 0) throw new IllegalOffsetArgumentException();

        return new ResponseEntity<>(categoryService.getCategories(limit, offset).stream()
                .map(categoryMapper::toCategoryDto), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryDto createCategoryDto) {
        Category category = categoryMapper.toCategory(createCategoryDto);
        categoryService.create(category);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
