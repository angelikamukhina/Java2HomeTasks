package exceptions;

public class UnableReadState extends Error {
    public UnableReadState(Throwable throwable) {
        super(throwable);
    }
}
