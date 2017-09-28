package ru.spbau.mit;

import java.util.function.Supplier;

public interface ThreadPool {
    <RetType> LightFuture<RetType> submit(Supplier<RetType> supplier);
    void shutdown();
}
