package com.merantory.dostavim.dto.mappers.person;

import com.merantory.dostavim.dto.impl.person.ChangePersonRoleDto;
import com.merantory.dostavim.dto.impl.person.PersonDto;
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

    public Person toPerson(ChangePersonRoleDto changePersonRoleDto) {
        Person person = new Person();
        person.setId(changePersonRoleDto.getId());
        String personRole = changePersonRoleDto.getRole();
        if (!personRole.startsWith("ROLE_")) {
            personRole = "ROLE_" + personRole;
        }
        person.setRole(personRole);

        return person;
    }

    public PersonDto toPersonDto(Person person) {
        PersonDto personDto = new PersonDto();
        personDto.setId(person.getId());
        personDto.setEmail(person.getEmail());
        personDto.setName(person.getName());
        personDto.setPhoneNumber(person.getPhoneNumber());
        personDto.setAddress(person.getAddress());
        personDto.setRole(person.getRole());

        return personDto;
    }
}
