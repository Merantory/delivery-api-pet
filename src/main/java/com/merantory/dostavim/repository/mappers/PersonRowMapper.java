package com.merantory.dostavim.repository.mappers;

import com.merantory.dostavim.model.Person;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PersonRowMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person person = new Person();
        person.setId(rs.getLong("id"));
        person.setEmail(rs.getString("email"));
        person.setPassword(rs.getString("password"));
        person.setName(rs.getString("name"));
        person.setPhoneNumber(rs.getString("phone_number"));
        person.setAddress(rs.getString("address"));
        person.setRole(rs.getString("role"));

        return person;
    }
}
