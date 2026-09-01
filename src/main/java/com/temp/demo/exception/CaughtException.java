package com.temp.demo.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CaughtException extends RuntimeException {
    private int code;

    public CaughtException(String message, int code) {
        super(message);
        this.code = code;
    }
}
