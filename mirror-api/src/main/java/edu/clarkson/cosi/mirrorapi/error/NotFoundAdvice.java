package edu.clarkson.cosi.mirrorapi.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * NotFoundAdvice specifies the response that will be sent to requests that
 * result in a NotFoundException being thrown.
 */
@ControllerAdvice
@SuppressWarnings("unused")
public class NotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String notFoundHandler(NotFoundException e) { return e.getMessage(); }
}
