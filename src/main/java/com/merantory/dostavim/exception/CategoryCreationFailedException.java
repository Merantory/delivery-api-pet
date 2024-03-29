package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoryCreationFailedException extends RuntimeException {
    public CategoryCreationFailedException() {
        super();
    }

    public CategoryCreationFailedException(String msg) {
        super(msg);
    }
}
