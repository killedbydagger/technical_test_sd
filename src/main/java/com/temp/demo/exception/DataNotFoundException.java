package com.temp.demo.exception;

public class DataNotFoundException extends CaughtException {
    public DataNotFoundException(String message) {
        super(message, 400);
    }
}
