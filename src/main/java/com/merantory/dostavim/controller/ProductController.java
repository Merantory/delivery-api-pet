package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.product.CreateProductDto;
import com.merantory.dostavim.dto.impl.product.ProductDto;
import com.merantory.dostavim.dto.mappers.product.ProductMapper;
import com.merantory.dostavim.exception.IllegalLimitArgumentException;
import com.merantory.dostavim.exception.IllegalOffsetArgumentException;
import com.merantory.dostavim.exception.IllegalRestaurantIdException;
import com.merantory.dostavim.exception.ProductNotFoundException;
import com.merantory.dostavim.model.Product;
import com.merantory.dostavim.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tags(
        value = {
                @Tag(name = "product-controller", description = "API для работы с товарами")
        }
)
@RestController
@RequestMapping("/products")
@Validated
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @Autowired
    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @Operation(
            description = "Возвращает товар, соответствующий идентификатору.",
            tags = {"get_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable("id") @Positive Long id) {
        Optional<Product> productOptional = productService.getProduct(id);
        if (productOptional.isEmpty()) throw new ProductNotFoundException();
        return new ResponseEntity<>(productMapper.toProductDto(productOptional.get()), HttpStatus.OK);
    }

    @Operation(
            description = "Возвращает массив всех товаров в ресторане с переданным идентификатором, " +
                    "если идентификатор не передан, возвращаются все товары в системе.",
            tags = {"get_method_endpoints"},
            parameters = {
                    @Parameter(name = "limit", in = ParameterIn.QUERY, description =
                            "Максимальное количество товаров в выдаче. " +
                                    "Если параметр не передан, то значение по умолчанию равно 1.",
                            required = false, style = ParameterStyle.SIMPLE),
                    @Parameter(name = "offset", in = ParameterIn.QUERY, description =
                            "Количество товаров, которое нужно пропустить для отображения текущей страницы. " +
                                    "Если параметр не передан, то значение по умолчанию равно 0.",
                            required = false, style = ParameterStyle.SIMPLE),
                    @Parameter(name = "restaurant_id", in = ParameterIn.QUERY, description =
                            "Идентефикатор ресторана, товары которого необходимо получить. " +
                                    "Если параметр не передан, возвращаются товары всех ресторанов.",
                            required = false, style = ParameterStyle.SIMPLE)
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(@RequestParam(value = "restaurant_id") Optional<Long> restaurantIdOptional,
                                                        @RequestParam(value = "limit") Optional<Integer> limitOptional,
                                                        @RequestParam(value = "offset") Optional<Integer> offsetOptional) {
        Integer limit = limitOptional.orElse(1);
        Integer offset = offsetOptional.orElse(0);

        if (limit < 1) throw new IllegalLimitArgumentException();
        if (offset < 0) throw new IllegalOffsetArgumentException();

        if (restaurantIdOptional.isPresent()) return getRestaurantProducts(restaurantIdOptional.get(), limit, offset);

        return new ResponseEntity<>(productService.getProducts(limit, offset).stream()
                .map(productMapper::toProductDto).toList(), HttpStatus.OK);
    }

    private ResponseEntity<List<ProductDto>> getRestaurantProducts(Long restaurantId, Integer limit, Integer offset) {
        if (restaurantId < 1) throw new IllegalRestaurantIdException();

        return new ResponseEntity<>(productService.getRestaurantProducts(restaurantId, limit, offset).stream()
                .map(productMapper::toProductDto).toList(), HttpStatus.OK);
    }

    @Operation(
            description = "Создание товара в системе.",
            summary = "Доступен только администраторам.",
            tags = {"post_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())})
    })
    @SecurityRequirement(name = "JWT Bearer Authentication")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductDto createProductDto) {
        Product product = productMapper.toProduct(createProductDto);
        product = productService.create(product);
        ProductDto productDto = productMapper.toProductDto(product);
        return new ResponseEntity<>(productDto, HttpStatus.CREATED);
    }

    @Operation(
            description = "Обновляет информация о товаре, с соответствующим идентификатором.",
            summary = "Доступен только администраторам.",
            tags = {"patch_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @SecurityRequirement(name = "JWT Bearer Authentication")
    @PatchMapping("/{id}/edit")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("id") @Positive Long id,
                                                    @Valid @RequestBody CreateProductDto createProductDto) {
        Product product = productMapper.toProduct(createProductDto);
        product.setId(id);
        product = productService.update(product);
        if (product == null) throw new ProductNotFoundException();
        ProductDto productDto = productMapper.toProductDto(product);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @Operation(
            description = "Удаляет продукт, соответствующим идентификатором из системы.",
            summary = "Доступен только администраторам.",
            tags = {"delete_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @SecurityRequirement(name = "JWT Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable("id") @Positive Long id) {
        Product deletedProduct = productService.delete(id);
        return new ResponseEntity<>(productMapper.toProductDto(deletedProduct), HttpStatus.OK);
    }
}
