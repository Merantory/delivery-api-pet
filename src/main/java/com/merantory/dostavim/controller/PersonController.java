package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.person.ChangePersonRoleDto;
import com.merantory.dostavim.dto.impl.person.PersonDto;
import com.merantory.dostavim.dto.impl.person.UpdatePersonInfoDto;
import com.merantory.dostavim.dto.mappers.person.PersonMapper;
import com.merantory.dostavim.exception.*;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.service.PersonService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tags(
        value = {
                @Tag(name = "person-controller", description = "API для работы с пользователями")
        }
)
@RestController
@RequestMapping("/users")
@Validated
public class PersonController {
    private final PersonService personService;
    private final PersonMapper personMapper;

    @Autowired
    public PersonController(PersonService personService, PersonMapper personMapper) {
        this.personService = personService;
        this.personMapper = personMapper;
    }

    @Operation(
            description = "Возвращает массив пользователей.",
            tags = {"get_method_endpoints"},
            parameters = {
                    @Parameter(name = "limit", in = ParameterIn.QUERY, description =
                            "Максимальное количество пользователей в выдаче. " +
                                    "Если параметр не передан, то значение по умолчанию равно 1.",
                            required = false, style = ParameterStyle.SIMPLE),
                    @Parameter(name = "offset", in = ParameterIn.QUERY, description =
                            "Количество пользователей, которое нужно пропустить для отображения текущей страницы. " +
                                    "Если параметр не передан, то значение по умолчанию равно 0.",
                            required = false, style = ParameterStyle.SIMPLE)
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
    })
    @GetMapping()
    public ResponseEntity<List<PersonDto>> getPersons(@RequestParam(value = "limit") Optional<Integer> limitOptional,
                                                      @RequestParam(value = "offset") Optional<Integer> offsetOptional) {
        Integer limit = limitOptional.orElse(1);
        Integer offset = offsetOptional.orElse(0);

        if (limit < 1) throw new IllegalLimitArgumentException();
        if (offset < 0) throw new IllegalOffsetArgumentException();

        return new ResponseEntity<>(personService.getPersons(limit, offset).stream()
                .map(personMapper::toPersonDto).toList(), HttpStatus.OK);
    }

    @Operation(
            description = "Возвращает пользователя, соответствующего идентификатору",
            tags = {"get_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
    public ResponseEntity<PersonDto> getPersons(@PathVariable @Positive Long id) {
        Optional<Person> personOptional = personService.getPerson(id);
        if (personOptional.isEmpty()) throw new PersonNotFoundException();
        return new ResponseEntity<>(personMapper.toPersonDto(personOptional.get()), HttpStatus.OK);
    }

    @Operation(
            description = "Меняет роль пользователя.",
            summary = "Доступен только администраторам.",
            tags = {"get_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @SecurityRequirement(name = "JWT Bearer Authentication")
    @PatchMapping("/change_role")
    public ResponseEntity<PersonDto> changePersonRole(@Valid @RequestBody ChangePersonRoleDto changePersonRoleDto) {
        Person person = personMapper.toPerson(changePersonRoleDto);
        person = personService.changeRole(person);
        return new ResponseEntity<>(personMapper.toPersonDto(person), HttpStatus.OK);
    }

    @Operation(
            description = "Обновление информации пользователя.",
            summary = "Доступен только авторизированным пользователям или администраторам.",
            tags = {"patch_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = {@Content(schema = @Schema(implementation = PersonDto.class),
                            mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
    })
    @SecurityRequirement(name = "JWT Bearer Authentication")
    @PatchMapping("/update_info")
    public ResponseEntity<?> updatePersonInfo(@Valid @RequestBody UpdatePersonInfoDto updatePersonInfoDto) {
        Person authPerson = getAuthenticationPerson();
        authPerson.setName(updatePersonInfoDto.getName());
        authPerson.setAddress(updatePersonInfoDto.getAddress());
        personService.update(authPerson);
        return new ResponseEntity<>(HttpStatus.OK);
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
