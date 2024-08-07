package ru.salfa.messenger.exception;

public class MessageTooLargeException extends RuntimeException {
    public MessageTooLargeException(String message) {
        super(message);
    }
}
