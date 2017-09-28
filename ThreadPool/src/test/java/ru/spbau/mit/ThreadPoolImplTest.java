package ru.spbau.mit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.*;

public class ThreadPoolImplTest {
    @Test
    public void checkThreadsNumber() throws Exception {
        ThreadPool threadPool = new ThreadPoolImpl(4);
        final int[] activeThreads = {0};
        ReentrantLock lock = new ReentrantLock();
        ArrayList<LightFuture<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            futures.add(threadPool.submit(() -> {
                lock.lock();
                activeThreads[0]++;
                lock.unlock();
                return true;
            }));
        }

        for (LightFuture<Boolean> future : futures) {
            assertTrue(future.get());
        }
        assertEquals(4, activeThreads[0]);
    }

    @Test
    public void execute() throws Exception {
        ThreadPoolImpl threadPool = new ThreadPoolImpl(8);

        List<LightFuture<Double>> futures = new ArrayList<>();
        double step = 1e-4;
        for (int i = 0; i < 50; i++) {
            futures.add(threadPool.submit(() -> {
                double sum = 0.0;
                for (double x = -200.0; x < 200.0; x += step) {
                    sum += step * Math.sin(x);
                }
                return sum;
                }));
        }

        double value = 0;
        for (LightFuture<Double> future : futures) {
            try {
                value += future.get();
            } catch (LightExecutionException e) {
                e.getCause();
            }
        }

        assertTrue(Math.abs(value) < 1e-2);
        threadPool.shutdown();
    }

    @Test
    public void shutdown() throws Exception {
        int initialThreads = Thread.activeCount();
        ThreadPool threadPool = new ThreadPoolImpl(10);
        threadPool.shutdown();
        int finalThreads = Thread.activeCount();
        assertEquals(initialThreads, finalThreads);
    }
}