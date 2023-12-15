package com.merantory.dostavim.repository;

import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.repository.mappers.PersonRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    public Optional<Person> getPerson(Long id) {
        String sqlQuery = "SELECT * FROM person WHERE id=?";
        Optional<Person> personOptional;
        try {
            personOptional = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, new PersonRowMapper(), id));
        } catch (EmptyResultDataAccessException emptyException) {
            personOptional = Optional.empty();
        }
        return personOptional;
    }

    public List<Person> getPersons(Integer limit, Integer offset) {
        String sqlQuery = "SELECT * FROM person LIMIT ? OFFSET ?";
        List<Person> personList = jdbcTemplate.query(sqlQuery, new PersonRowMapper(), limit, offset);
        return personList;
    }

    public Boolean save(Person person) {
        String sqlQuery = "INSERT INTO person(email, password, name, phone_number, address, role) VALUES(?, ?, ?, ?, ?, ?)";
        Boolean isSaved = (jdbcTemplate.update(sqlQuery, person.getEmail(), person.getPassword(),
                    person.getName(), person.getPhoneNumber(), person.getAddress(), person.getRole())) != 0;
        return isSaved;
    }

    public Boolean updatePersonInfo(Person person) {
        String sqlQuery = "UPDATE person SET name=?, address=? WHERE id=?";
        Boolean isUpdated = (jdbcTemplate.update(sqlQuery, person.getName(), person.getAddress(), person.getId())) != 0;
        return isUpdated;
    }

    public Boolean delete(Long id) {
        String sqlQuery = "DELETE FROM person WHERE id=?";
        Boolean isDeleted = (jdbcTemplate.update(sqlQuery, id)) != 0;
        return isDeleted;
    }

    public Boolean changeRole(Person person) {
        String sqlQuery = "UPDATE person SET role=? WHERE id=?";
        Boolean isChanged = (jdbcTemplate.update(sqlQuery, person.getRole(), person.getId())) != 0;
        return isChanged;
    }
}
