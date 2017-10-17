package ru.spbau.mit;

import java.util.function.Function;

public interface LightFuture<ReturnType> {
    boolean isReady();

    ReturnType get() throws LightExecutionException, InterruptedException;

    <FunRetType> LightFuture<FunRetType> thenApply(Function<? super ReturnType, ? extends FunRetType> func) throws LightExecutionException;
}
