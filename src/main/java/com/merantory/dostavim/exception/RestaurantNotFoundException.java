package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException() {
        super();
    }

    public RestaurantNotFoundException(String msg) {
        super(msg);
    }
}
