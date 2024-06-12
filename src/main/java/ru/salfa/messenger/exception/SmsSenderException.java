package ru.salfa.messenger.exception;

public class SmsSenderException extends RuntimeException {
    public SmsSenderException(String message) {
        super(message);
    }
}