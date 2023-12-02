package com.merantory.dostavim.util.validator;

import com.merantory.dostavim.exception.PersonAlreadyExistException;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class SignUpPersonValidator implements Validator {
    private final PersonService personService;

    @Autowired
    public SignUpPersonValidator(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person signUpPerson = (Person) target;
        Optional<Person> personOptional = personService.getPersonByEmail(signUpPerson.getEmail());
        if (personOptional.isPresent()) {
            throw new PersonAlreadyExistException();
        }
    }

    @Override
    public Errors validateObject(Object target) {
        return Validator.super.validateObject(target);
    }
}
