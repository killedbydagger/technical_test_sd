package com.temp.demo.bean;

import com.fasterxml.jackson.core.JsonParseException;
import com.temp.demo.dto.response.BasicResponse;
import com.temp.demo.exception.CaughtException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerAdviceConfig {

    private final Logger logger = LogManager.getLogger(this);

    /* Handling Mismatch Request Parameter */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BasicResponse<String>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        logger.warn("MethodArgumentTypeMismatchException has occurred");
        String parameterType = ex.getParameter().getParameterType().getSimpleName();
        String type = "";
        if(parameterType.equalsIgnoreCase("Long") ||
                parameterType.equals("Integer") ||
                parameterType.equalsIgnoreCase("Double") ||
                parameterType.equals("int")) {
            type =  "number";
        }else if(parameterType.equals("String")) {
            type = "string";
        }
        String message = String.format("Request parameter %s should be %s", ex.getParameter().getParameterName(), type);
        return processError(message, HttpStatus.BAD_REQUEST);
    }

    /* Handling Missing Request Parameter */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<BasicResponse<String>> handleMissingRequestParameterException(MissingServletRequestParameterException ex) {
        logger.warn("MissingServletRequestParameterException has occurred");
        String message = String.format("Request parameter %s is required", ex.getParameterName());
        return processError(message, HttpStatus.BAD_REQUEST);
    }

    /* Handling Missing Request Header Parameter */
    @ExceptionHandler(value = MissingRequestHeaderException.class)
    public ResponseEntity<BasicResponse<String>> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        logger.warn("MissingRequestHeaderException has occurred");
        String message = String.format("Request header %s is required", ex.getHeaderName());
        return processError(message, HttpStatus.BAD_REQUEST);
    }

    /* Handling Missing Path Variable Parameter */
    @ExceptionHandler(value = MissingPathVariableException.class)
    public ResponseEntity<BasicResponse<String>> handleMissingPathVariableException(MissingPathVariableException ex) {
        logger.warn("MissingPathVariableException has occurred");
        String message = String.format("Path variable %s is required", ex.getVariableName());
        return processError(message, HttpStatus.BAD_REQUEST);
    }

    /* Handling Validation JSON */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<BasicResponse<String>> handleJSONValidationException(MethodArgumentNotValidException ex) {
        logger.warn("MethodArgumentNotValidException has occurred");
        String message = ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", ", "", ""));
        return processError(message, HttpStatus.BAD_REQUEST);
    }

    /* Handling Validation Request Parameter */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<BasicResponse<String>> handleRequestParameterValidationException(ConstraintViolationException ex) {
        logger.warn("ConstraintViolationException has occurred");
        String message = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", ", "", ""));
        return processError(message, HttpStatus.BAD_REQUEST);
    }

    /* Handling JSON Mapping Error */
    @ExceptionHandler(value = JsonParseException.class)
    public ResponseEntity<BasicResponse<String>> handleJSONParseException(JsonParseException ex) {
        logger.warn("JsonParseException has occurred");
        String message = "JSON body is invalid, error at converting JSON";
        return processError(message, HttpStatus.BAD_REQUEST);
    }

    /* Handling Http Method Error */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BasicResponse<String>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        logger.warn("HttpRequestMethodNotSupportedException has occurred");
        StringJoiner joiner = new StringJoiner(", ");
        if(!Objects.isNull(ex.getSupportedHttpMethods())) {
            ex.getSupportedHttpMethods().forEach(httpMethod -> joiner.add(httpMethod.name()));
        }
        String message = String.format("This end point only supported http method %s", joiner);
        return processError(message, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /* Handling Resource Not Found */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ResponseEntity<BasicResponse<String>> handleNoResourceException(NoHandlerFoundException ex) {
        logger.warn("NoHandlerFoundException has occurred");
        String message = String.format("Path '%s' with '%s' method is not found", ex.getRequestURL(), ex.getHttpMethod());
        return processError(message, HttpStatus.NOT_FOUND);
    }

    /* Handling Access Denied */
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<BasicResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("AccessDeniedException has occurred");
        return processError(ex.getMessage(), HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<BasicResponse<String>> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.warn("HttpMessageNotReadableException has occurred");
        return processError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CaughtException.class)
    public ResponseEntity<BasicResponse<String>> handleCaughtException(CaughtException ex) {
        logger.warn("CaughtException has occurred");
        return processError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<BasicResponse<String>> handleCaughtException(Exception ex) {
        logger.warn("Exception has occurred");
        return processError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<BasicResponse<String>> processError(String message, HttpStatus status) {
        BasicResponse<String> basicResponse = new BasicResponse<>();
        basicResponse.setFailed(message);
        return ResponseEntity.status(status).body(basicResponse);
    }
}
