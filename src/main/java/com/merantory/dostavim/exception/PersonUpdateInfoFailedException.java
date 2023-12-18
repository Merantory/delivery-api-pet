package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PersonUpdateInfoFailedException extends RuntimeException {
    public PersonUpdateInfoFailedException() {
        super();
    }

    public PersonUpdateInfoFailedException(String msg) {
        super(msg);
    }
}
