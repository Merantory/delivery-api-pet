package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.comment.CommentDto;
import com.merantory.dostavim.dto.impl.comment.CreateCommentDto;
import com.merantory.dostavim.dto.mappers.comment.CommentMapper;
import com.merantory.dostavim.exception.CommentNotFoundException;
import com.merantory.dostavim.exception.IllegalLimitArgumentException;
import com.merantory.dostavim.exception.IllegalOffsetArgumentException;
import com.merantory.dostavim.exception.PersonAuthFailedException;
import com.merantory.dostavim.model.Comment;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.service.CommentService;
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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tags(
		value = {
				@Tag(name = "comment-controller", description = "API для работы с комментариями товаров")
		}
)
@RestController
@RequestMapping("/comments")
@Validated
public class CommentController {
	private final CommentService commentService;
	private final CommentMapper commentMapper;
	private static final String INVALID_LIMIT_MESSAGE =
			"Invalid limit argument value. Its should be positive. Received: %d";
	private static final String INVALID_OFFSET_MESSAGE =
			"Invalid offset argument value. Its should be not negative. Received: %d";

	@Autowired
	public CommentController(CommentService commentService, CommentMapper commentMapper) {
		this.commentService = commentService;
		this.commentMapper = commentMapper;
	}

	@Operation(
			description = "Возвращает комментарий, соответствующий идентификатору.",
			tags = {"get_method_endpoints"}
	)
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
	@GetMapping("/{id}")
	public ResponseEntity<CommentDto> getComment(@PathVariable("id") @Positive Long id) {
		Optional<Comment> commentOptional = commentService.getComment(id);
		if (commentOptional.isEmpty()) {
			throw new CommentNotFoundException();
		}
		return new ResponseEntity<>(commentMapper.toCommentDto(commentOptional.get()), HttpStatus.OK);
	}

	@Operation(
			description = "Возвращает массив всех комментариев в системе.",
			tags = {"get_method_endpoints"},
			parameters = {
					@Parameter(name = "limit", in = ParameterIn.QUERY, description =
							"Максимальное количество комментариев в выдаче. " +
									"Если параметр не передан, то значение по умолчанию равно 1.",
							required = false, style = ParameterStyle.SIMPLE),
					@Parameter(name = "offset", in = ParameterIn.QUERY, description =
							"Количество комментариев, которое нужно пропустить для отображения текущей страницы. " +
									"Если параметр не передан, то значение по умолчанию равно 0.",
							required = false, style = ParameterStyle.SIMPLE)
			}
	)
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
	@GetMapping
	public ResponseEntity<List<CommentDto>> getComments(@RequestParam(value = "limit") Optional<Integer> limitOptional,
														@RequestParam(value = "offset") Optional<Integer> offsetOptional) {
		Integer limit = limitOptional.orElse(1);
		Integer offset = offsetOptional.orElse(0);

		if (limit < 1) throw new IllegalLimitArgumentException(
				String.format(INVALID_LIMIT_MESSAGE, limit));
		if (offset < 0) throw new IllegalOffsetArgumentException(
				String.format(INVALID_OFFSET_MESSAGE, offset));

		return new ResponseEntity<>(commentService.getComments(limit, offset).stream().map(commentMapper::toCommentDto).toList(), HttpStatus.OK);
	}

	@Operation(
			description = "Возвращает массив всех комментариев к товару, с соответствующим идентификатором в системе.",
			tags = {"get_method_endpoints"},
			parameters = {
					@Parameter(name = "limit", in = ParameterIn.QUERY, description =
							"Максимальное количество комментариев в выдаче. " +
									"Если параметр не передан, то значение по умолчанию равно 1.",
							required = false, style = ParameterStyle.SIMPLE),
					@Parameter(name = "offset", in = ParameterIn.QUERY, description =
							"Количество комментариев, которое нужно пропустить для отображения текущей страницы. " +
									"Если параметр не передан, то значение по умолчанию равно 0.",
							required = false, style = ParameterStyle.SIMPLE),
					@Parameter(name = "product_id", in = ParameterIn.QUERY, description =
							"Идентификатор товара, комментарии которого необходимо получить.",
							required = true, style = ParameterStyle.SIMPLE)
			}
	)
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
	@GetMapping("/products/{product_id}")
	public ResponseEntity<List<CommentDto>> getProductComments(@PathVariable("product_id") @Positive Long productId,
															   @RequestParam(value = "limit") Optional<Integer> limitOptional,
															   @RequestParam(value = "offset") Optional<Integer> offsetOptional) {
		Integer limit = limitOptional.orElse(1);
		Integer offset = offsetOptional.orElse(0);

		if (limit < 1) throw new IllegalLimitArgumentException(
				String.format(INVALID_LIMIT_MESSAGE, limit));
		if (offset < 0) throw new IllegalOffsetArgumentException(
				String.format(INVALID_OFFSET_MESSAGE, offset));

		return new ResponseEntity<>(commentService.getProductComments(productId, limit, offset).stream().map(commentMapper::toCommentDto).toList(), HttpStatus.OK);
	}

	@Operation(
			description = "Возвращает массив всех комментариев пользователя, с соответствующим идентификатором в системе.",
			tags = {"get_method_endpoints"},
			parameters = {
					@Parameter(name = "limit", in = ParameterIn.QUERY, description =
							"Максимальное количество комментариев в выдаче. " +
									"Если параметр не передан, то значение по умолчанию равно 1.",
							required = false, style = ParameterStyle.SIMPLE),
					@Parameter(name = "offset", in = ParameterIn.QUERY, description =
							"Количество комментариев, которое нужно пропустить для отображения текущей страницы. " +
									"Если параметр не передан, то значение по умолчанию равно 0.",
							required = false, style = ParameterStyle.SIMPLE),
					@Parameter(name = "person", in = ParameterIn.QUERY, description =
							"Идентификатор пользователя, комментарии которого необходимо получить.",
							required = true, style = ParameterStyle.SIMPLE)
			}
	)
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
	@GetMapping("/users/{person_id}")
	public ResponseEntity<List<CommentDto>> getPersonComments(@PathVariable("person_id") @Positive Long personId,
															  @RequestParam(value = "limit") Optional<Integer> limitOptional,
															  @RequestParam(value = "offset") Optional<Integer> offsetOptional) {
		Integer limit = limitOptional.orElse(1);
		Integer offset = offsetOptional.orElse(0);

		if (limit < 1) throw new IllegalLimitArgumentException(
				String.format(INVALID_LIMIT_MESSAGE, limit));
		if (offset < 0) throw new IllegalOffsetArgumentException(
				String.format(INVALID_OFFSET_MESSAGE, offset));

		return new ResponseEntity<>(commentService.getPersonComments(personId, limit, offset).stream().map(commentMapper::toCommentDto).toList(), HttpStatus.OK);
	}

	@Operation(
			description = "Создание комментария от лица текущего авторизированного пользователя.",
			summary = "Доступен только авторизированным пользователям или администраторам.",
			tags = {"post_method_endpoints"}
	)
	@ApiResponse(responseCode = "201")
	@ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
	@ApiResponse(responseCode = "409", content = {@Content(schema = @Schema())})
	@SecurityRequirement(name = "JWT Bearer Authentication")
	@PostMapping()
	public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CreateCommentDto createCommentDto) {
		Comment comment = commentMapper.toComment(createCommentDto);
		Person creator = getAuthenticationPerson();
		comment.setCreator(creator);
		comment = commentService.create(comment);
		return new ResponseEntity<>(commentMapper.toCommentDto(comment), HttpStatus.CREATED);
	}

	private Person getAuthenticationPerson() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
			return (Person) authentication.getPrincipal();
		} else {
			throw new PersonAuthFailedException("Person authentication failed");
		}
	}
}
