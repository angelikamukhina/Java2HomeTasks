package ru.spbau.mit.exceptions;

public class UnableGetMessageException extends Error {
    public UnableGetMessageException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
