package com.temp.demo.exception;

public class DataErrorException extends CaughtException {
    public DataErrorException(String message) {
        super(message, 400);
    }
}
