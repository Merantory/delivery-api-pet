package com.merantory.dostavim.controller;

import com.merantory.dostavim.dto.impl.person.UpdatePersonInfoDto;
import com.merantory.dostavim.exception.PersonAuthFailedException;
import com.merantory.dostavim.exception.PersonUpdateFailedException;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class PersonController {
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/update_info")
    public ResponseEntity<?> updatePersonInfo(@RequestBody UpdatePersonInfoDto updatePersonInfoDto) {
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
