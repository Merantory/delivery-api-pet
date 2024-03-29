package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RestaurantUpdateFailedException extends RuntimeException {
    public RestaurantUpdateFailedException() {
        super();
    }

    public RestaurantUpdateFailedException(String msg) {
        super(msg);
    }
}
