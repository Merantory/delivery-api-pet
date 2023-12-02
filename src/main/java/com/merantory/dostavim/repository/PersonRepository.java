package com.merantory.dostavim.repository;

import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.repository.mappers.PersonRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PersonRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Person> getByEmail(String email) {
        String sqlQuery = "SELECT * FROM person WHERE email=?";
        Optional<Person> personOptional;
        try {
            personOptional = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, new PersonRowMapper(), email));
        } catch (EmptyResultDataAccessException emptyException) {
            personOptional = Optional.empty();
        }
        return personOptional;
    }

    public Boolean save(Person person) {
        String sqlQuery = "INSERT INTO person(email, password, name, phone_number, address, role) VALUES(?, ?, ?, ?, ?, ?)";
        Boolean isSaved = (jdbcTemplate.update(sqlQuery, person.getEmail(), person.getPassword(),
                    person.getName(), person.getPhoneNumber(), person.getAddress(), person.getRole())) != 0;
        return isSaved;
    }

}
