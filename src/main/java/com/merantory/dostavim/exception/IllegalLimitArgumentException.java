package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalLimitArgumentException extends IllegalArgumentException {
    public IllegalLimitArgumentException() {
        super();
    }

    public IllegalLimitArgumentException(String msg) {
        super(msg);
    }
}
