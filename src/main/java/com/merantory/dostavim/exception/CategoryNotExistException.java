package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoryNotExistException extends RuntimeException {
    public CategoryNotExistException() {
        super();
    }

    public CategoryNotExistException(String msg) {
        super(msg);
    }
}
