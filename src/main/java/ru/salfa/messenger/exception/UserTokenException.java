package ru.salfa.messenger.exception;

public class UserTokenException extends RuntimeException {
    public UserTokenException(String message) {
        super(message);
    }
}