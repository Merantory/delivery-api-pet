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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tags(
        value = {
                @Tag(name = "restaurant-controller", description = "API для работы с ресторанами")
        }
)
@RestController
@RequestMapping("/restaurants")
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
            description = "Возвращает ресторан, соответствующий идентификатору.",
            tags = {"get_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = {@Content(schema = @Schema(implementation = RestaurantDto.class),
                            mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurant(@PathVariable Long id) {
        Optional<Restaurant> restaurantOptional = restaurantService.getRestaurant(id);
        if (restaurantOptional.isEmpty()) throw new RestaurantNotFoundException();
        return new ResponseEntity<>(restaurantMapper.toRestaurantDto(restaurantOptional.get()), HttpStatus.OK);
    }

    @Operation(
            description = "Возвращает ресторан, соответствующий идентификатору.",
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
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantDto.class)),
                            mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @GetMapping
    public ResponseEntity<?> getRestaurants(@RequestParam(value = "limit") Optional<Integer> limitOptional,
                                            @RequestParam(value = "offset") Optional<Integer> offsetOptional) {
        Integer limit = limitOptional.orElse(1);
        Integer offset = offsetOptional.orElse(0);

        if (limit < 1) throw new IllegalLimitArgumentException();
        if (offset < 0) throw new IllegalOffsetArgumentException();

        return new ResponseEntity<>(restaurantService.getRestaurants(limit, offset).stream()
                .map(restaurantMapper::toRestaurantDto).toList(), HttpStatus.OK);
    }

    @Operation(
            description = "Добавляет продукты в определенном количестве в ресторан, с соответствующим идентификатором. " +
                    "Если данных продуктов не существовало в ресторане до этого, они создатся с указанным количеством, " +
                    "иначе количество имеющихся продуктов увеличится на указанное в запросе.",
            tags = {"post_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/add_product")
    public ResponseEntity<?> addOrUpdateProducts(@RequestBody AddProductToRestaurantDto addProductToRestaurantDto) {
        ProductRestaurant productRestaurant = productRestaurantMapper.toProductRestaurant(addProductToRestaurantDto);
        Boolean isApplied = restaurantService.addOrUpdateProduct(productRestaurant);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            description = "Создает ресторан в системе.",
            tags = {"post_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = {@Content(schema = @Schema(implementation = RestaurantDto.class),
                            mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())})
    })
    @PostMapping
    public ResponseEntity<?> createRestaurant(@RequestBody CreateRestaurantDto createRestaurantDto) {
        Restaurant restaurant = restaurantMapper.toRestaurant(createRestaurantDto);
        Boolean isCreated = restaurantService.create(restaurant);
        return (isCreated) ? new ResponseEntity<>(HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Operation(
            description = "Обновляет информация о ресторане, с соответствующим идентификатором.",
            tags = {"patch_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = {@Content(schema = @Schema(implementation = RestaurantDto.class),
                            mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PatchMapping("/{id}/edit")
    public ResponseEntity<?> updateRestaurant(@PathVariable("id") Long id,
                                              @RequestBody CreateRestaurantDto createRestaurantDto) {
        Restaurant restaurant = restaurantMapper.toRestaurant(createRestaurantDto);
        restaurant.setId(id);
        Boolean isUpdated = restaurantService.update(restaurant);
        if (!isUpdated) throw new RestaurantNotFoundException();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            description = "Удаляет ресторан, соответствующий идентификатору из системы.",
            tags = {"delete_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable("id") Long id) {
        Boolean isDeleted = restaurantService.delete(id);
        if (!isDeleted) throw new RestaurantNotFoundException();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
