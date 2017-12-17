package exceptions;

public class InternalPeerException extends Error {
    public InternalPeerException(Throwable throwable) {
        super(throwable);
    }
}
