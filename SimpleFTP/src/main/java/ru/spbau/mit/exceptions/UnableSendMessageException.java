package ru.spbau.mit.exceptions;

public class UnableSendMessageException extends Error {
    public UnableSendMessageException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
