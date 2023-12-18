package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.exception.PersonAlreadyExistException;
import com.merantory.dostavim.exception.PersonNotFoundException;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.repository.PersonRepository;
import com.merantory.dostavim.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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
    public Person loadUserByUsername(String username) throws UsernameNotFoundException {
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
        if (isExistPersonWithEmail(person)) {
            throw new PersonAlreadyExistException(String.format("Peron with this email %s already exist.",
                    person.getEmail()));
        }
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
        if (!isExistPerson(id)) {
            throw new PersonNotFoundException(String.format("Person with id %d not found.", id));
        }
        return personRepository.delete(id);
    }

    @Override
    @Transactional
    public Person changeRole(Person person) {
        Boolean isChanged = personRepository.changeRole(person);
        if (isChanged) {
            return getPerson(person.getId()).get();
        }
        throw new PersonNotFoundException(String.format("Person with id %d not found.", person.getId()));
    }

    private Boolean isExistPersonWithEmail(Person person) {
        return isExistPersonWithEmail(person.getEmail());
    }

    private Boolean isExistPersonWithEmail(String email) {
        Optional<Person> personOptional = personRepository.getByEmail(email);
        return personOptional.isPresent();
    }

    private Boolean isExistPerson(Person person) {
        return isExistPerson(person.getId());
    }

    private Boolean isExistPerson(Long id) {
        Optional<Person> personOptional = personRepository.getPerson(id);
        return personOptional.isPresent();
    }
}
