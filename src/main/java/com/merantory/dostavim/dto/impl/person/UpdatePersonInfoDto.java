package com.merantory.dostavim.dto.impl.person;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePersonInfoDto {
    private String name;
    private String address;
}
