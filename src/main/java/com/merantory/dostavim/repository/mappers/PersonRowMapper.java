package com.merantory.dostavim.repository.mappers;

import com.merantory.dostavim.model.Person;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;

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
        try {
            person.setDeletedAt((rs.getTimestamp("deleted_at",
                            Calendar.getInstance(TimeZone.getTimeZone("UTC")))
                    .toInstant()));
        } catch (Exception ignore) {}

        return person;
    }
}
