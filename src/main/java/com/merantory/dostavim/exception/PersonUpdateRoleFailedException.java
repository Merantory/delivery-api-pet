package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PersonUpdateRoleFailedException extends RuntimeException {
    public PersonUpdateRoleFailedException() {
        super();
    }

    public PersonUpdateRoleFailedException(String msg) {
        super(msg);
    }
}
