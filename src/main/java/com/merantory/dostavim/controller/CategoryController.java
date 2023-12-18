package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.category.CategoryDto;
import com.merantory.dostavim.dto.impl.category.CreateCategoryDto;
import com.merantory.dostavim.dto.mappers.category.CategoryMapper;
import com.merantory.dostavim.exception.IllegalLimitArgumentException;
import com.merantory.dostavim.exception.IllegalOffsetArgumentException;
import com.merantory.dostavim.model.Category;
import com.merantory.dostavim.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tags(
		value = {
				@Tag(name = "category-controller", description = "API для работы с категориями")
		}
)
@RestController
@RequestMapping("/categories")
@Validated
public class CategoryController {
	private final CategoryService categoryService;
	private final CategoryMapper categoryMapper;

	@Autowired
	public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
		this.categoryService = categoryService;
		this.categoryMapper = categoryMapper;
	}

	@Operation(
			description = "Возвращает массив всех категорий в системе.",
			tags = {"get_method_endpoints"},
			parameters = {
					@Parameter(name = "limit", in = ParameterIn.QUERY, description =
							"Максимальное количество категорий в выдаче. " +
									"Если параметр не передан, то значение по умолчанию равно 1.",
							required = false, style = ParameterStyle.SIMPLE),
					@Parameter(name = "offset", in = ParameterIn.QUERY, description =
							"Количество категорий, которое нужно пропустить для отображения текущей страницы. " +
									"Если параметр не передан, то значение по умолчанию равно 0.",
							required = false, style = ParameterStyle.SIMPLE)
			}
	)
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
	@GetMapping
	public ResponseEntity<List<CategoryDto>> getCategories(@RequestParam(value = "limit") Optional<Integer> limitOptional,
														   @RequestParam(value = "offset") Optional<Integer> offsetOptional) {
		Integer limit = limitOptional.orElse(1);
		Integer offset = offsetOptional.orElse(0);

		if (limit < 1) throw new IllegalLimitArgumentException(
				String.format("Invalid limit argument value. Its should be positive. Received: %d", limit));
		if (offset < 0) throw new IllegalOffsetArgumentException(
				String.format("Invalid offset argument value. Its should be not negative. Received: %d", offset));

		return new ResponseEntity<>(categoryService.getCategories(limit, offset).stream()
				.map(categoryMapper::toCategoryDto).toList(), HttpStatus.OK);
	}

	@Operation(
			description = "Создание категории в системе.",
			summary = "Доступен только администраторам.",
			tags = {"post_method_endpoints"}
	)
	@ApiResponse(responseCode = "201")
	@ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "409", content = {@Content(schema = @Schema())})
	@SecurityRequirement(name = "JWT Bearer Authentication")
	@PostMapping
	public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryDto createCategoryDto) {
		Category category = categoryMapper.toCategory(createCategoryDto);
		category = categoryService.create(category);
		CategoryDto categoryDto = categoryMapper.toCategoryDto(category);
		return new ResponseEntity<>(categoryDto, HttpStatus.CREATED);
	}
}
