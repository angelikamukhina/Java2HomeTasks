package exceptions;

public class UnableStoreState extends Error {
    public UnableStoreState(Throwable throwable) {
        super(throwable);
    }
}
