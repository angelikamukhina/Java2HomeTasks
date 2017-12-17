package exceptions;

public class InternalTrackerException extends Error {
    public InternalTrackerException(Throwable throwable) {
        super(throwable);
    }

    public InternalTrackerException(String message) {
        super(message);
    }
}
