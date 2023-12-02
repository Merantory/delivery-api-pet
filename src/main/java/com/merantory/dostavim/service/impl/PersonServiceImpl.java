package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.repository.PersonRepository;
import com.merantory.dostavim.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> personOptional = personRepository.getByEmail(username);
        if (personOptional.isEmpty()) {
            throw new BadCredentialsException("User with this email not found");
        }
        return personOptional.get();
    }

    @Override
    public Optional<Person> getPersonByEmail(String email) {
        Optional<Person> personOptional = personRepository.getByEmail(email);
        return personOptional;
    }

    @Override
    public Optional<Person> getPerson(Long id) {
        Optional<Person> personOptional = personRepository.getPerson(id);
        return personOptional;
    }

    @Override
    public List<Person> getPersons(Integer limit, Integer offset) {
        return personRepository.getPersons(limit, offset);
    }

    @Override
    @Transactional
    public Boolean signUp(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        return personRepository.save(person);
    }

    @Override
    @Transactional
    public Boolean update(Person person) {
        return personRepository.updatePersonInfo(person);
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        return personRepository.delete(id);
    }
}
