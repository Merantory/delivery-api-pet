package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.order.CreateOrderDto;
import com.merantory.dostavim.dto.impl.order.OrderDto;
import com.merantory.dostavim.dto.mappers.order.OrderMapper;
import com.merantory.dostavim.exception.*;
import com.merantory.dostavim.model.Order;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tags(
        value = {
                @Tag(name = "order-controller", description = "API для работы с заказами")
        }
)
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @Operation(
            description = "Возвращает заказ, соответствующий идентификатору.",
            tags = {"get_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        Person person = getAuthenticationPerson();

        Optional<Order> orderOptional = orderService.getOrder(id);

        if (orderOptional.isEmpty()) {
            throw new OrderNotFoundException();
        }
        Order order = orderOptional.get();
        if (!person.getRole().equals("ROLE_ADMIN") && !order.getPerson().getId().equals(person.getId())) {
            throw new ForbiddenException();
        }

        return new ResponseEntity<>(orderMapper.toOrderDto(orderOptional.get()), HttpStatus.OK);
    }

    @Operation(
            description = "Возвращает массив всех заказов в системе.",
            tags = {"get_method_endpoints"},
            parameters = {
                    @Parameter(name = "limit", in = ParameterIn.QUERY, description =
                            "Максимальное количество заказов в выдаче. " +
                                    "Если параметр не передан, то значение по умолчанию равно 1.",
                            required = false, style = ParameterStyle.SIMPLE),
                    @Parameter(name = "offset", in = ParameterIn.QUERY, description =
                            "Количество заказов, которое нужно пропустить для отображения текущей страницы. " +
                                    "Если параметр не передан, то значение по умолчанию равно 0.",
                            required = false, style = ParameterStyle.SIMPLE),
                    @Parameter(name = "detailed", in = ParameterIn.QUERY, description =
                            "Необходимость в передаче списка продуктов заказа. " +
                                    "Если параметр не передан, то значение по умолчанию равно false",
                            required = false, style = ParameterStyle.SIMPLE)
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/all")
    public ResponseEntity<List<OrderDto>> getOrders(@RequestParam(value = "limit") Optional<Integer> limitOptional,
                                                    @RequestParam(value = "offset") Optional<Integer> offsetOptional,
                                                    @RequestParam(value = "detailed") Optional<Boolean> detailedOptional) {
        Integer limit = limitOptional.orElse(1);
        Integer offset = offsetOptional.orElse(0);
        Boolean detailed = detailedOptional.orElse(false);

        if (limit < 1) throw new IllegalLimitArgumentException();
        if (offset < 0) throw new IllegalOffsetArgumentException();

        return new ResponseEntity<>(orderService.getOrders(limit, offset, detailed).stream()
                .map(orderMapper::toOrderDto).toList(), HttpStatus.OK);
    }

    @Operation(
            description = "Возвращает массив заказов авторизированного пользователя.",
            tags = {"get_method_endpoints"},
            parameters = {
            @Parameter(name = "limit", in = ParameterIn.QUERY, description =
                    "Максимальное количество заказов в выдаче. " +
                            "Если параметр не передан, то значение по умолчанию равно 1.",
                    required = false, style = ParameterStyle.SIMPLE),
            @Parameter(name = "offset", in = ParameterIn.QUERY, description =
                    "Количество заказов, которое нужно пропустить для отображения текущей страницы. " +
                            "Если параметр не передан, то значение по умолчанию равно 0.",
                    required = false, style = ParameterStyle.SIMPLE),
            @Parameter(name = "detailed", in = ParameterIn.QUERY, description =
                    "Необходимость в передаче списка продуктов заказа. " +
                            "Если параметр не передан, то значение по умолчанию равно true",
                    required = false, style = ParameterStyle.SIMPLE)
    }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
    })
    @GetMapping()
    public ResponseEntity<List<OrderDto>> getPersonOrders(@RequestParam(value = "limit") Optional<Integer> limitOptional,
                                             @RequestParam(value = "offset") Optional<Integer> offsetOptional,
                                             @RequestParam(value = "detailed") Optional<Boolean> detailedOptional) {
        Integer limit = limitOptional.orElse(1);
        Integer offset = offsetOptional.orElse(0);
        Boolean detailed = detailedOptional.orElse(true);

        if (limit < 1) throw new IllegalLimitArgumentException();
        if (offset < 0) throw new IllegalOffsetArgumentException();
        Long ordersOwnerId = getAuthenticationPerson().getId();

        return new ResponseEntity<>(orderService.getPersonOrders(ordersOwnerId, limit, offset, detailed).stream()
                .map(orderMapper::toOrderDto).toList(), HttpStatus.OK);
    }

    @Operation(
            description = "Создание заказа для текущего авторизированного пользователя.",
            tags = {"post_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
    })
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody CreateOrderDto createOrderDto) {
        Person creator = getAuthenticationPerson();
        Order order = orderMapper.toOrder(createOrderDto);
        order.setPerson(creator);
        Order createdOrder = orderService.create(order);
        return new ResponseEntity<>(orderMapper.toOrderDto(createdOrder), HttpStatus.CREATED);
    }

    private Person getAuthenticationPerson() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return (Person) authentication.getPrincipal();
        } else {
            throw new PersonAuthFailedException("Person authentication failed");
        }
    }
}
