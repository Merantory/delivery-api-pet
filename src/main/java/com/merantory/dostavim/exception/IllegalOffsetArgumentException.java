package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalOffsetArgumentException extends IllegalArgumentException {
    public IllegalOffsetArgumentException() {
        super();
    }

    public IllegalOffsetArgumentException(String msg) {
        super(msg);
    }
}
