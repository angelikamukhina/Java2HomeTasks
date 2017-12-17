package exceptions;

public class UnableParseQueryException extends Error {
    public UnableParseQueryException(Throwable throwable) {
        super(throwable);
    }

    public UnableParseQueryException(String message) {
        super(message);
    }
}
