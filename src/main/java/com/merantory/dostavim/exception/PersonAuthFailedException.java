package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class PersonAuthFailedException extends AuthenticationException {
    public PersonAuthFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public PersonAuthFailedException(String msg) {
        super(msg);
    }
}
