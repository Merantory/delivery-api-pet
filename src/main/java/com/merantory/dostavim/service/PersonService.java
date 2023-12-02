package com.merantory.dostavim.service;

import com.merantory.dostavim.model.Person;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface PersonService extends UserDetailsService {
    Optional<Person> getPersonByEmail(String email);
    Optional<Person> getPerson(Long id);
    List<Person> getPersons(Integer limit, Integer offset);
    Boolean signUp(Person person);
    Boolean update(Person person);
    Boolean delete(Long id);
}
