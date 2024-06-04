package ru.salfa.messenger.controller.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.salfa.messenger.dto.response.ErrorResponse;
import ru.salfa.messenger.exception.*;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> userAlreadyExistsException(UserAlreadyExistsException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(UserTokenException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserTokenException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserBlockedException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(UserOtpException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserOtpException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}