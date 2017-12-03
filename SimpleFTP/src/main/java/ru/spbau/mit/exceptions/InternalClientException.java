package ru.spbau.mit.exceptions;

public class InternalClientException extends Error {
    public InternalClientException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
