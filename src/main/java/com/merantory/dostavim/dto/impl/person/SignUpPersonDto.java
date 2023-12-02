package com.merantory.dostavim.dto.impl.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpPersonDto {
    private String email;
    private String password;
    private String name;
    @JsonProperty("phone_number")
    private String phoneNumber;
}
