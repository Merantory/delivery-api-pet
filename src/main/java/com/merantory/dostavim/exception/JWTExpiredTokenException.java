package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JWTExpiredTokenException extends RuntimeException {
    public JWTExpiredTokenException() {
        super();
    }

    public JWTExpiredTokenException(String msg) {
        super(msg);
    }
}
