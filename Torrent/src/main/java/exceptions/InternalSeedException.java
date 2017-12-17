package exceptions;

public class InternalSeedException extends Error {
    public InternalSeedException(Throwable throwable) {
        super(throwable);
    }
}
