package com.merantory.dostavim.dto.impl.person;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private String address;
    private String role;
}
