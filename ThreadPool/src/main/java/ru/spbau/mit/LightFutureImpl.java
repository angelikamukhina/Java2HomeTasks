package ru.spbau.mit;

import java.util.function.Function;

public class LightFutureImpl<RetType> implements LightFuture<RetType> {
    private final ThreadPoolImpl threadPool;
    private RetType result = null;
    private LightExecutionException exception = null;

    LightFutureImpl(ThreadPoolImpl threadPool) {
        this.threadPool = threadPool;
    }

    synchronized void setResult(RetType result) {
        this.result = result;
        notify();
    }

    synchronized void setException(LightExecutionException exception) {
        this.exception = exception;
        notify();
    }

    @Override
    public boolean isReady() {
        return result != null || exception != null;
    }

    @Override
    public synchronized RetType get() throws LightExecutionException {
        if (!isReady()) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        if (exception != null) {
            throw exception;
        }
        return result;
    }

    @Override
    public <FunRetType> LightFuture<FunRetType> thenApply(Function<? super RetType, ? extends FunRetType> func) {
        LightFutureImpl<FunRetType> newFuture = new LightFutureImpl<>(threadPool);
        Runnable nextTask = () -> {
            try {
                RetType privResult = get();
                if (privResult != null) {
                    newFuture.setResult(func.apply(privResult));
                }
            } catch (Exception e) {
                newFuture.setException(new LightExecutionException(e));
            }
        };
        threadPool.pushTaskToQueue(nextTask);
        return newFuture;
    }
}
