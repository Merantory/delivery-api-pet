package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductDeleteFailedException extends RuntimeException {
    public ProductDeleteFailedException() {
        super();
    }

    public ProductDeleteFailedException(String msg) {
        super(msg);
    }
}
