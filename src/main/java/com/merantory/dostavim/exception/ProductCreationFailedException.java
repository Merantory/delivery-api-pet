package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductCreationFailedException extends RuntimeException {
    public ProductCreationFailedException() {
        super();
    }

    public ProductCreationFailedException(String msg) {
        super(msg);
    }
}
