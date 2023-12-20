package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.order.CreateOrderDto;
import com.merantory.dostavim.dto.impl.order.OrderDto;
import com.merantory.dostavim.dto.mappers.order.OrderMapper;
import com.merantory.dostavim.exception.ForbiddenException;
import com.merantory.dostavim.exception.IllegalLimitArgumentException;
import com.merantory.dostavim.exception.IllegalOffsetArgumentException;
import com.merantory.dostavim.exception.OrderNotFoundException;
import com.merantory.dostavim.model.Order;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.service.OrderService;
import com.merantory.dostavim.util.security.AuthenticationHelper;
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
				@Tag(name = "order-controller", description = "API для работы с заказами")
		}
)
@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {
	private final OrderService orderService;
	private final OrderMapper orderMapper;
	private final AuthenticationHelper authenticationHelper;
	private static final String INVALID_LIMIT_MESSAGE =
			"Invalid limit argument value. Its should be positive. Received: %d";
	private static final String INVALID_OFFSET_MESSAGE =
			"Invalid offset argument value. Its should be not negative. Received: %d";

	@Autowired
	public OrderController(OrderService orderService, OrderMapper orderMapper,
						   AuthenticationHelper authenticationHelper) {
		this.orderService = orderService;
		this.orderMapper = orderMapper;
		this.authenticationHelper = authenticationHelper;
	}

	@Operation(
			description = "Возвращает заказ, с соответствующим идентификатором.",
			summary = "Доступен только администраторам.",
			parameters = {
					@Parameter(name = "id", in = ParameterIn.PATH, description =
							"Идентификатор заказа, информацию о котором необходимо вернуть.",
							required = true, style = ParameterStyle.SIMPLE)
			}
	)
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
	@SecurityRequirement(name = "JWT Bearer Authentication")
	@GetMapping("/{id}")
	public ResponseEntity<OrderDto> getOrder(@PathVariable("id") @Positive Long id) {
		Person person = authenticationHelper.getAuthenticationPerson();

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
			summary = "Доступен только администраторам.",
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
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())})
	@SecurityRequirement(name = "JWT Bearer Authentication")
	@GetMapping("/all")
	public ResponseEntity<List<OrderDto>> getOrders(@RequestParam(value = "limit") Optional<Integer> limitOptional,
													@RequestParam(value = "offset") Optional<Integer> offsetOptional,
													@RequestParam(value = "detailed") Optional<Boolean> detailedOptional) {
		Integer limit = limitOptional.orElse(1);
		Integer offset = offsetOptional.orElse(0);
		Boolean detailed = detailedOptional.orElse(false);

		if (limit < 1) throw new IllegalLimitArgumentException(
				String.format(INVALID_LIMIT_MESSAGE, limit));
		if (offset < 0) throw new IllegalOffsetArgumentException(
				String.format(INVALID_OFFSET_MESSAGE, offset));

		return new ResponseEntity<>(orderService.getOrders(limit, offset, detailed).stream()
				.map(orderMapper::toOrderDto).toList(), HttpStatus.OK);
	}

	@Operation(
			description = "Возвращает массив заказов авторизированного пользователя.",
			summary = "Доступен только авторизированным пользователям и администраторам.",
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
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
	@SecurityRequirement(name = "JWT Bearer Authentication")
	@GetMapping()
	public ResponseEntity<List<OrderDto>> getPersonOrders(@RequestParam(value = "limit") Optional<Integer> limitOptional,
														  @RequestParam(value = "offset") Optional<Integer> offsetOptional,
														  @RequestParam(value = "detailed") Optional<Boolean> detailedOptional) {
		Integer limit = limitOptional.orElse(1);
		Integer offset = offsetOptional.orElse(0);
		Boolean detailed = detailedOptional.orElse(true);

		if (limit < 1) throw new IllegalLimitArgumentException(
				String.format(INVALID_LIMIT_MESSAGE, limit));
		if (offset < 0) throw new IllegalOffsetArgumentException(
				String.format(INVALID_OFFSET_MESSAGE, offset));
		Long ordersOwnerId = authenticationHelper.getAuthenticationPerson().getId();

		return new ResponseEntity<>(orderService.getPersonOrders(ordersOwnerId, limit, offset, detailed).stream()
				.map(orderMapper::toOrderDto).toList(), HttpStatus.OK);
	}

	@Operation(
			description = "Создание заказа для текущего авторизированного пользователя.",
			summary = "Доступен только авторизированным пользователям или администраторам."
	)
	@ApiResponse(responseCode = "201")
	@ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
	@SecurityRequirement(name = "JWT Bearer Authentication")
	@PostMapping
	public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderDto createOrderDto) {
		Person creator = authenticationHelper.getAuthenticationPerson();
		Order order = orderMapper.toOrder(createOrderDto);
		order.setPerson(creator);
		Order createdOrder = orderService.create(order);
		return new ResponseEntity<>(orderMapper.toOrderDto(createdOrder), HttpStatus.CREATED);
	}
}
