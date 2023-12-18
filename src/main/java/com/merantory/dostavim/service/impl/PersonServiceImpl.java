package com.merantory.dostavim.service.impl;

import com.merantory.dostavim.exception.PersonAlreadyExistException;
import com.merantory.dostavim.exception.PersonNotFoundException;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.repository.PersonRepository;
import com.merantory.dostavim.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
        log.info("Trying to load person with email={}", username);
        Optional<Person> personOptional = personRepository.getByEmail(username);
        if (personOptional.isEmpty()) {
            log.info("Person with email={} not found", username);
            throw new BadCredentialsException("User with this email not found");
        }
        return personOptional.get();
    }

    @Override
    public Optional<Person> getPersonByEmail(String email) {
        log.info("Trying to load person with email={}", email);
        Optional<Person> personOptional = personRepository.getByEmail(email);
        return personOptional;
    }

    @Override
    public Optional<Person> getPerson(Long id) {
        log.info("Trying to load person with id={}", id);
        Optional<Person> personOptional = personRepository.getPerson(id);
        return personOptional;
    }

    @Override
    public List<Person> getPersons(Integer limit, Integer offset) {
        log.info("Trying to load person with limit={} and offset={}", limit, offset);
        return personRepository.getPersons(limit, offset);
    }

    @Override
    @Transactional
    public Boolean signUp(Person person) {
        log.info("Trying to sign up person: {}", person);
        if (isExistPersonWithEmail(person)) {
            log.info("Person with email={} already exist", person.getEmail());
            throw new PersonAlreadyExistException(String.format("Person with this email %s already exist.",
                    person.getEmail()));
        }
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        Boolean isSaved = personRepository.save(person);
        log.info("Person has been created: {}", person);
        return isSaved;
    }

    @Override
    @Transactional
    public Person update(Person person) {
        log.info("Trying to update person with id={} on data: {}", person.getId(), person);
        if (!isExistPerson(person)) {
            log.info("Person with id={} not found", person.getId());
            throw new PersonNotFoundException(String.format("Person with id %d not found.", person.getId()));
        }
        Boolean isUpdated = personRepository.updatePersonInfo(person);
        Optional<Person> updatedPersonOptional = getPerson(person.getId());
        log.info("Person has been updated: {}", updatedPersonOptional.get());
        return updatedPersonOptional.get();
    }

    @Override
    @Transactional
    public Person delete(Long id) {
        log.info("Trying to delete person with id={}", id);
        Optional<Person> personOptional = getPerson(id);
        if (personOptional.isEmpty()) {
            log.info("Person with id={} not found", id);
            throw new PersonNotFoundException(String.format("Person with id %d not found.", id));
        }
        personRepository.delete(id);
        return personOptional.get();
    }

    @Override
    @Transactional
    public Person changeRole(Person person) {
        log.info("Trying to change role for person with id={} on new role={}", person.getId(), person.getRole());
        if (!isExistPerson(person.getId())) {
            log.info("Person with id={} not found", person.getId());
            throw new PersonNotFoundException(String.format("Person with id %d not found.", person.getId()));
        }
        Boolean isChanged = personRepository.changeRole(person);
        person = getPerson(person.getId()).get();
        log.info("Person role has been changed: {}", person);
        return person;
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
