package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.productRestaurant.AddProductToRestaurantDto;
import com.merantory.dostavim.dto.impl.restaurant.CreateRestaurantDto;
import com.merantory.dostavim.dto.impl.restaurant.RestaurantDto;
import com.merantory.dostavim.dto.mappers.productRestaurant.ProductRestaurantMapper;
import com.merantory.dostavim.dto.mappers.restaurant.RestaurantMapper;
import com.merantory.dostavim.exception.IllegalLimitArgumentException;
import com.merantory.dostavim.exception.IllegalOffsetArgumentException;
import com.merantory.dostavim.exception.RestaurantNotFoundException;
import com.merantory.dostavim.model.ProductRestaurant;
import com.merantory.dostavim.model.Restaurant;
import com.merantory.dostavim.service.RestaurantService;
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
                @Tag(name = "restaurant-controller", description = "API для работы с ресторанами")
        }
)
@RestController
@RequestMapping("/restaurants")
@Validated
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;
    private final ProductRestaurantMapper productRestaurantMapper;

    @Autowired
    public RestaurantController(RestaurantService restaurantService, RestaurantMapper restaurantMapper,
                                ProductRestaurantMapper productRestaurantMapper) {
        this.restaurantService = restaurantService;
        this.restaurantMapper = restaurantMapper;
        this.productRestaurantMapper = productRestaurantMapper;
    }

    @Operation(
            description = "Возвращает ресторан, соответствующим идентификатором.",
            tags = {"get_method_endpoints"}
    )
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getRestaurant(@PathVariable @Positive Long id) {
        Optional<Restaurant> restaurantOptional = restaurantService.getRestaurant(id);
        if (restaurantOptional.isEmpty()) throw new RestaurantNotFoundException();
        return new ResponseEntity<>(restaurantMapper.toRestaurantDto(restaurantOptional.get()), HttpStatus.OK);
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<RestaurantDto> getRestaurantWithProducts(@PathVariable Long id) {
        Optional<Restaurant> restaurantOptional = restaurantService.getRestaurantWithProducts(id);
        if (restaurantOptional.isEmpty()) throw new RestaurantNotFoundException();
        return new ResponseEntity<>(restaurantMapper.toRestaurantDto(restaurantOptional.get()), HttpStatus.OK);
    }

    @Operation(
            description = "Возвращает ресторан, с соответствующим идентификатором.",
            tags = {"get_method_endpoints"},
            parameters = {
                    @Parameter(name = "limit", in = ParameterIn.QUERY, description =
                            "Максимальное количество ресторанов в выдаче. " +
                                    "Если параметр не передан, то значение по умолчанию равно 1.",
                            required = false, style = ParameterStyle.SIMPLE),
                    @Parameter(name = "offset", in = ParameterIn.QUERY, description =
                            "Количество ресторанов, которое нужно пропустить для отображения текущей страницы. " +
                                    "Если параметр не передан, то значение по умолчанию равно 0.",
                            required = false, style = ParameterStyle.SIMPLE)
            }
    )
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @GetMapping
    public ResponseEntity<List<RestaurantDto>> getRestaurants(@RequestParam(value = "limit") Optional<Integer> limitOptional,
                                                              @RequestParam(value = "offset") Optional<Integer> offsetOptional) {
        Integer limit = limitOptional.orElse(1);
        Integer offset = offsetOptional.orElse(0);

        if (limit < 1) throw new IllegalLimitArgumentException(
                String.format("Invalid limit argument value. Its should be positive. Received: %d", limit));
        if (offset < 0) throw new IllegalOffsetArgumentException(
                String.format("Invalid offset argument value. Its should be not negative. Received: %d", offset));

        return new ResponseEntity<>(restaurantService.getRestaurants(limit, offset).stream()
                .map(restaurantMapper::toRestaurantDto).toList(), HttpStatus.OK);
    }

    @Operation(
            description = "Добавляет продукты в определенном количестве в ресторан, с соответствующим идентификатором. " +
                    "Если данных продуктов не существовало в ресторане до этого, они создаются с указанным количеством, " +
                    "иначе количество имеющихся продуктов в ресторане увеличится на указанное в запросе.",
            summary = "Доступен только администраторам.",
            tags = {"post_method_endpoints"}
    )
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @SecurityRequirement(name = "JWT Bearer Authentication")
    @PostMapping("/add_product")
    public ResponseEntity<RestaurantDto> addOrUpdateProducts(@Valid @RequestBody AddProductToRestaurantDto addProductToRestaurantDto) {
        ProductRestaurant productRestaurant = productRestaurantMapper.toProductRestaurant(addProductToRestaurantDto);
        Restaurant restaurant = restaurantService.addOrUpdateProduct(productRestaurant);
        RestaurantDto restaurantDto = restaurantMapper.toRestaurantDto(restaurant);
        return new ResponseEntity<>(restaurantDto, HttpStatus.OK);
    }

    @Operation(
            description = "Создает ресторан в системе.",
            summary = "Доступен только администраторам.",
            tags = {"post_method_endpoints"}
    )
    @ApiResponse(responseCode = "201")
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())})
    @SecurityRequirement(name = "JWT Bearer Authentication")
    @PostMapping
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody CreateRestaurantDto createRestaurantDto) {
        Restaurant restaurant = restaurantMapper.toRestaurant(createRestaurantDto);
        restaurant = restaurantService.create(restaurant);
        return new ResponseEntity<>(restaurantMapper.toRestaurantDto(restaurant), HttpStatus.CREATED);
    }

    @Operation(
            description = "Обновляет информация о ресторане, с соответствующим идентификатором.",
            summary = "Доступен только администраторам.",
            tags = {"patch_method_endpoints"}
    )
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @SecurityRequirement(name = "JWT Bearer Authentication")
    @PatchMapping("/{id}/edit")
    public ResponseEntity<RestaurantDto> updateRestaurant(@PathVariable("id") @Positive Long id,
                                                          @Valid @RequestBody CreateRestaurantDto createRestaurantDto) {
        Restaurant restaurant = restaurantMapper.toRestaurant(createRestaurantDto);
        restaurant.setId(id);
        restaurant = restaurantService.update(restaurant);
        return new ResponseEntity<>(restaurantMapper.toRestaurantDto(restaurant), HttpStatus.OK);
    }

    @Operation(
            description = "Удаляет ресторан, соответствующий идентификатору из системы.",
            summary = "Доступен только администраторам.",
            tags = {"delete_method_endpoints"}
    )
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @SecurityRequirement(name = "JWT Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<RestaurantDto> deleteRestaurant(@PathVariable("id") @Positive Long id) {
        Restaurant restaurant = restaurantService.delete(id);
        return new ResponseEntity<>(restaurantMapper.toRestaurantDto(restaurant), HttpStatus.OK);
    }
}
