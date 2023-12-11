package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.person.PersonDto;
import com.merantory.dostavim.dto.impl.person.UpdatePersonInfoDto;
import com.merantory.dostavim.exception.PersonAuthFailedException;
import com.merantory.dostavim.exception.PersonUpdateFailedException;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.service.PersonService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

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

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @Operation(
            description = "Обновление информации пользователя.",
            tags = {"patch_method_endpoints"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = {@Content(schema = @Schema(implementation = PersonDto.class),
                            mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema())})
    })
    @PatchMapping("/update_info")
    public ResponseEntity<?> updatePersonInfo(@Valid @RequestBody UpdatePersonInfoDto updatePersonInfoDto) {
        Person authPerson = getAuthenticationPerson();
        authPerson.setName(updatePersonInfoDto.getName());
        authPerson.setAddress(updatePersonInfoDto.getAddress());
        System.out.println(authPerson.getId());
        Boolean isUpdated = personService.update(authPerson);
        if (!isUpdated) throw new PersonUpdateFailedException();
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
