package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.*;

public class LightFutureImplTest {

    @Test
    public void isReady() throws Exception {
        ThreadPool threadPool = new ThreadPoolImpl(4);
        LightFuture<Double> sinIntegral = threadPool.submit(() -> sinIntegral(-1, 1, 1e-4));

        assertFalse(sinIntegral.isReady());
        sinIntegral.get();
        assertTrue(sinIntegral.isReady());
    }

    @Test
    public void get() throws Exception {
        ThreadPool threadPool = new ThreadPoolImpl(4);
        LightFuture<Double> sinIntegral = threadPool.submit(() -> sinIntegral(-1, 1, 1e-4));

        assertTrue(Math.abs(sinIntegral.get()) < 1e-5);
    }

    @Test
    public void testGetReturnsNull() throws Exception {
        ThreadPool threadPool = new ThreadPoolImpl(4);
        LightFuture<Object> futureWithNull = threadPool.submit(() -> null);
        assertNull(futureWithNull.get());
    }

    @Test(expected = LightExecutionException.class)
    public void getWithException() throws Exception {
        ThreadPool threadPool = new ThreadPoolImpl(4);
        LightFuture<Double> future = threadPool.submit(() -> {
            throw new ArithmeticException();
        });
        future.get();
    }

    @Test
    public void thenApply() throws Exception {

        ThreadPool threadPool = new ThreadPoolImpl(4);
        for (int i = 0; i < 1e3; i++) {
            threadPool.submit(() -> {
                Integer sum = 0;
                for (Integer integer = 0; integer < 1e6; integer++) {
                    sum += integer;
                }
                return sum;
            });
        }
        LightFuture<Double> future = threadPool.submit(() -> 2.0);
        for (int i = 2; i < 10; i++) {
            future.thenApply(d -> d * 2);
        }
        LightFuture<Double> waitingFuture = future.thenApply(d -> d * 2);
        double res = waitingFuture.get();
        assertTrue(Math.abs(res - 4) < 1e-5);
    }

    double sinIntegral(double left, double right, double step) {
        double res = 0.0;
        for (double x = left; x < right; x += step) {
            res += step * Math.sin(x);
        }
        return res;
    }
}