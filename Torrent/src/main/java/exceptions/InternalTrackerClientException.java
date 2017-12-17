package exceptions;

public class InternalTrackerClientException extends Error {
    public InternalTrackerClientException(Throwable throwable) {
        super(throwable);
    }
    public InternalTrackerClientException(String message) {
        super(message);
    }
}
