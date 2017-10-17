package ru.spbau.mit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {
    private final Queue<Runnable> workQueue = new LinkedList<>();
    private final List<Thread> threads = new ArrayList<>();
    private volatile boolean isWorking = true;

    public ThreadPoolImpl(int n) {
        for (int i = 0; i < n; i++) {
            Thread thread = new Thread(new Worker());
            thread.start();
            threads.add(thread);
        }
    }

    @Override
    public <ReturnType> LightFuture<ReturnType> submit(Supplier<ReturnType> supplier) {
        if (!isWorking) {
            return null;
        }
        LightFutureImpl<ReturnType> future = new LightFutureImpl<>(this);
        synchronized (workQueue) {
            Runnable newTask = () -> {
                try {
                    future.setResult(supplier.get());
                } catch (Exception e) {
                    future.setException(new LightExecutionException(e));
                }
            };
            pushTaskToQueue(newTask);
        }
        return future;
    }

    public void shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
        isWorking = false;
    }

    void pushTaskToQueue(Runnable task) {
        synchronized (workQueue) {
            workQueue.add(task);
            workQueue.notify();
        }
    }

    private final class Worker implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Runnable nextTask;
                    synchronized (workQueue) {
                        if (workQueue.isEmpty()) {
                            workQueue.wait();
                        }
                        nextTask = workQueue.poll();
                    }
                    if (nextTask != null) {
                        nextTask.run();
                    }
                }
            } catch (InterruptedException e) {
            }
        }
    }
}