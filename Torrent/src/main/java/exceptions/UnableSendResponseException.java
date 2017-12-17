package exceptions;

public class UnableSendResponseException extends Error {
    public UnableSendResponseException(Throwable throwable) {
        super(throwable);
    }
}
