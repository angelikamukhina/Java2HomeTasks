package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        LightFuture<Double> future = threadPool.submit(() -> 2.0);
        for (int deg = 2; deg < 10; deg++) {
            future = future.thenApply(d -> d * 2);
            assertTrue(future.get() - Math.pow(2, deg) < 1e-4);
        }
    }

    double sinIntegral(double left, double right, double step) {
        double res = 0.0;
        for (double x = left; x < right; x += step) {
            res += step * Math.sin(x);
        }
        return res;
    }
}