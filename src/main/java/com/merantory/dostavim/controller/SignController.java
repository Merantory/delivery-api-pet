package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.person.SignInPersonDto;
import com.merantory.dostavim.dto.impl.person.SignUpPersonDto;
import com.merantory.dostavim.dto.mappers.person.PersonMapper;
import com.merantory.dostavim.exception.PersonAuthFailedException;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.security.JwtUtil;
import com.merantory.dostavim.service.PersonService;
import com.merantory.dostavim.util.validator.SignUpPersonValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tags(
        value = {
                @Tag(name = "sign-controller", description = "API для работы с регистрацией")
        }
)
@RestController
@RequestMapping("/sign")
@Validated
public class SignController {
    private final AuthenticationManager authenticationManager;
    private final PersonService personService;
    private final PersonMapper personMapper;
    private final SignUpPersonValidator signUpPersonValidator;
    private final JwtUtil jwtUtil;

    @Autowired
    public SignController(AuthenticationManager authenticationManager, PersonService personService,
                          PersonMapper personMapper, SignUpPersonValidator signUpPersonValidator, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.personService = personService;
        this.personMapper = personMapper;
        this.signUpPersonValidator = signUpPersonValidator;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            description = "Авторизация пользователя в системе с возвращением JWT токена.",
            summary = "Доступен только не авторизированным пользователям.",
            tags = {"post_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(type = "object", example = "{\"jwt_token\":\"value\"}"))}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/in")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInPersonDto signInPersonDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signInPersonDto.getEmail(), signInPersonDto.getPassword());
        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException exception) {
            throw new PersonAuthFailedException("Incorrect credentials");
        }
        String token = jwtUtil.generateToken(signInPersonDto.getEmail());
        return new ResponseEntity<>(Map.of("jwt_token", token), HttpStatus.OK);
    }

    @Operation(
            description = "Регистрация пользователя в системе с возвращением JWT токена.",
            summary = "Доступен только не авторизированным пользователям.",
            tags = {"post_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(type = "object", example = "{\"jwt_token\":\"value\"}"))}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpPersonDto signUpPersonDto,
                                                          BindingResult bindingResult) {
        Person person = personMapper.toPerson(signUpPersonDto);
        signUpPersonValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
        {
            return new ResponseEntity(bindingResult, HttpStatus.BAD_REQUEST);
        }
        personService.signUp(person);
        String token = jwtUtil.generateToken(person.getEmail());

        return new ResponseEntity<>(Map.of("jwt_token", token), HttpStatus.CREATED);
    }
}
