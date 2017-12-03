package ru.spbau.mit.exceptions;

public class InternalServerException extends Error {
    public InternalServerException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
