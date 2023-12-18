package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RestaurantAddProductFailedException extends RuntimeException {
    public RestaurantAddProductFailedException() {
        super();
    }

    public RestaurantAddProductFailedException(String msg) {
        super(msg);
    }
}
