package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PersonCreationFailedException extends RuntimeException {
    public PersonCreationFailedException() {
        super();
    }

    public PersonCreationFailedException(String msg) {
        super(msg);
    }
}
