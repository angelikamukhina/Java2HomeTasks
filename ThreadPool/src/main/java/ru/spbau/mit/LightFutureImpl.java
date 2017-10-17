package ru.spbau.mit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LightFutureImpl<ReturnType> implements LightFuture<ReturnType> {
    private final ThreadPoolImpl threadPool;
    private volatile ReturnType result = null;
    private volatile LightExecutionException exception = null;
    private volatile boolean isReady = false;
    private List<Runnable> waitingTasks = new ArrayList<>();

    LightFutureImpl(ThreadPoolImpl threadPool) {
        this.threadPool = threadPool;
    }

    synchronized void setResult(ReturnType result) {
        this.result = result;
        isReady = true;
        submitWaitingTasks();
        notifyAll();
    }

    synchronized void setException(LightExecutionException exception) {
        this.exception = exception;
        isReady = true;
        submitWaitingTasks();
        notifyAll();
    }

    private synchronized void submitWaitingTasks() {
        for (Runnable task : waitingTasks) {
            threadPool.pushTaskToQueue(task);
        }
        waitingTasks.clear();
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public synchronized ReturnType get() throws LightExecutionException {
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
    public <FunctionReturnType> LightFuture<FunctionReturnType>
    thenApply(Function<? super ReturnType, ? extends FunctionReturnType> func) {
        LightFutureImpl<FunctionReturnType> newFuture = new LightFutureImpl<>(threadPool);
        Runnable nextTask = () -> {
            try {
                ReturnType previousResult = get();
                newFuture.setResult(func.apply(previousResult));
            } catch (Exception e) {
                newFuture.setException(new LightExecutionException(e));
            }
        };
        synchronized (this) {
            if (!isReady) {
                waitingTasks.add(nextTask);
            } else {
                threadPool.pushTaskToQueue(nextTask);
            }
        }

        return newFuture;
    }
}
