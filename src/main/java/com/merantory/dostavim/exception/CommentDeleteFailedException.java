package com.merantory.dostavim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommentDeleteFailedException extends RuntimeException {
	public CommentDeleteFailedException() {
		super();
	}

	public CommentDeleteFailedException(String msg) {
		super(msg);
	}
}
