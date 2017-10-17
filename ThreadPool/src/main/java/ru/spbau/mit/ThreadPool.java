package ru.spbau.mit;

import java.util.function.Supplier;

public interface ThreadPool {
    <ReturnType> LightFuture<ReturnType> submit(Supplier<ReturnType> supplier);

    void shutdown();
}
