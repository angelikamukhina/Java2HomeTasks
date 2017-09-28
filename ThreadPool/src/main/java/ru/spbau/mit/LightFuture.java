package ru.spbau.mit;

import java.util.function.Function;

public interface LightFuture<RetType> {
    boolean isReady();
    RetType get() throws LightExecutionException, InterruptedException;
    <FunRetType> LightFuture<FunRetType> thenApply(Function<? super RetType, ? extends FunRetType> func) throws LightExecutionException;
}
