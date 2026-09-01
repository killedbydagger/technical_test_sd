package com.temp.demo.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationException extends CaughtException {
    public AuthenticationException(String message) {
        super(message, 401);
    }
}
