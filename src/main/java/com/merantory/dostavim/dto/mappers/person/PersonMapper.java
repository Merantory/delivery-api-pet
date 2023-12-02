package com.merantory.dostavim.dto.mappers.person;

import com.merantory.dostavim.dto.impl.person.SignUpPersonDto;
import com.merantory.dostavim.model.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {
    public Person toPerson(SignUpPersonDto signUpPersonDto) {
        Person person = new Person();
        person.setEmail(signUpPersonDto.getEmail());
        person.setPassword(signUpPersonDto.getPassword());
        person.setName(signUpPersonDto.getName());
        person.setPhoneNumber(signUpPersonDto.getPhoneNumber());
        person.setRole("ROLE_USER");
        return person;
    }
}
