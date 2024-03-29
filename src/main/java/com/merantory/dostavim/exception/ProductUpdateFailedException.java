package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductUpdateFailedException extends RuntimeException {
    public ProductUpdateFailedException() {
        super();
    }

    public ProductUpdateFailedException(String msg) {
        super(msg);
    }
}
