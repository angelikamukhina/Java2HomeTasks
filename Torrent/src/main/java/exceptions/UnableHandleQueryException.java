package exceptions;

public class UnableHandleQueryException extends Error {
    public UnableHandleQueryException(Throwable throwable) {
        super(throwable);
    }
}
