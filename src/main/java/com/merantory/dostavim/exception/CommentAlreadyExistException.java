package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CommentAlreadyExistException extends RuntimeException {
}
