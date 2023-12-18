package com.merantory.dostavim;

import com.merantory.dostavim.controller.CategoryController;
import com.merantory.dostavim.dto.mappers.category.CategoryMapper;
import com.merantory.dostavim.exception.IllegalLimitArgumentException;
import com.merantory.dostavim.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryControllerTests {
    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCategoriesWithInvalidLimitArgument() {
        Integer invalidLimit = 0;
        assertThrows(IllegalLimitArgumentException.class, () -> categoryController.getCategories(Optional.of(invalidLimit), Optional.of(0)));
    }
}
