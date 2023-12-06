package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.productRestaurant.AddProductToRestaurantDto;
import com.merantory.dostavim.dto.impl.restaurant.CreateRestaurantDto;
import com.merantory.dostavim.dto.mappers.productRestaurant.ProductRestaurantMapper;
import com.merantory.dostavim.dto.mappers.restaurant.RestaurantMapper;
import com.merantory.dostavim.exception.IllegalLimitArgumentException;
import com.merantory.dostavim.exception.IllegalOffsetArgumentException;
import com.merantory.dostavim.exception.RestaurantNotFoundException;
import com.merantory.dostavim.model.ProductRestaurant;
import com.merantory.dostavim.model.Restaurant;
import com.merantory.dostavim.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurant(@PathVariable Long id) {
        Optional<Restaurant> restaurantOptional = restaurantService.getRestaurant(id);
        if (restaurantOptional.isEmpty()) throw new RestaurantNotFoundException();
        return new ResponseEntity<>(restaurantMapper.toRestaurantDto(restaurantOptional.get()), HttpStatus.OK);
    }

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

    @PostMapping("/add_product")
    public ResponseEntity<?> addOrUpdateProducts(@RequestBody AddProductToRestaurantDto addProductToRestaurantDto) {
        ProductRestaurant productRestaurant = productRestaurantMapper.toProductRestaurant(addProductToRestaurantDto);
        Boolean isApplied = restaurantService.addOrUpdateProduct(productRestaurant);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createRestaurant(@RequestBody CreateRestaurantDto createRestaurantDto) {
        Restaurant restaurant = restaurantMapper.toRestaurant(createRestaurantDto);
        Boolean isCreated = restaurantService.create(restaurant);
        return (isCreated) ? new ResponseEntity<>(HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/{id}/edit")
    public ResponseEntity<?> updateRestaurant(@PathVariable("id") Long id,
                                              @RequestBody CreateRestaurantDto createRestaurantDto) {
        Restaurant restaurant = restaurantMapper.toRestaurant(createRestaurantDto);
        restaurant.setId(id);
        Boolean isUpdated = restaurantService.update(restaurant);
        if (!isUpdated) throw new RestaurantNotFoundException();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable("id") Long id) {
        Boolean isDeleted = restaurantService.delete(id);
        if (!isDeleted) throw new RestaurantNotFoundException();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
