package ru.spbau.mit.java;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.*;

public class LockFreeListImplTest {

    private final int THREADS_NUMBER = 1000;

    private static LockFreeList<Integer> fillWithSerialIntegers(int maxNumber) {
        LockFreeList<Integer> intList = new LockFreeListImpl<>();
        for (int i = 0; i < maxNumber; ++i) {
            intList.append(i);
        }
        return intList;
    }

    @Test
    public void simpleIsEmptyTest() throws Exception {
        LockFreeList<Integer> intList = new LockFreeListImpl<>();
        assertTrue(intList.isEmpty());
    }

    @Test
    public void simpleAppendTest() throws Exception {
        LockFreeList<Integer> intList = fillWithSerialIntegers(THREADS_NUMBER);
        for (int i = 0; i < THREADS_NUMBER; ++i) {
            assertTrue(intList.contains(i));
        }
    }

    @Test
    public void appendMultiThreadTest() throws Exception {
        final LockFreeList<Integer> intList = new LockFreeListImpl<>();
        ArrayList<Thread> threadsList = new ArrayList<>();

        CyclicBarrier barrier = new CyclicBarrier(THREADS_NUMBER);
        for (int i = 0; i < THREADS_NUMBER; ++i) {
            int number = i;
            threadsList.add(new Thread(() -> {
                try {
                    barrier.await();
                } catch (Throwable e) {
                }
                intList.append(number);
            }));
        }
        for (Thread thread : threadsList) {
            thread.start();
        }
        for (Thread thread : threadsList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }

        for (int i = 0; i < THREADS_NUMBER; i++) {
            assertTrue(intList.contains(i));
        }
    }

    @Test
    public void simpleRemoveTest() throws Exception {
        LockFreeList<Integer> intList = new LockFreeListImpl<>();
        for (int i = 0; i < THREADS_NUMBER; i++) {
            intList.append(i);
        }

        for (int i = 0; i < THREADS_NUMBER; i++) {
            intList.remove(i);
        }
        assertTrue(intList.isEmpty());
    }

    @Test
    public void multithreadRemoveDiffNumbersTest() throws Exception {
        LockFreeList<Integer> intList = fillWithSerialIntegers(THREADS_NUMBER);
        ArrayList<Thread> threadsList = new ArrayList<>();
        CyclicBarrier barrier = new CyclicBarrier(THREADS_NUMBER);

        for (int i = 0; i < THREADS_NUMBER; ++i) {
            int number = i;
            threadsList.add(new Thread(() -> {
                try {
                    barrier.await();
                } catch (Throwable t) {
                }
                intList.remove(number);
            }));
        }

        for (Thread thread : threadsList) {
            thread.start();
        }
        for (Thread thread : threadsList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
        assertTrue(intList.isEmpty());
    }

    @Test
    public void multithreadRemoveOneNumberTest() throws Exception {
        LockFreeList<Integer> intList = new LockFreeListImpl<>();
        int numberInList = 1;
        intList.append(numberInList);
        ArrayList<Thread> threadsList = new ArrayList<>();
        CopyOnWriteArrayList<Boolean> results = new CopyOnWriteArrayList<>();
        CyclicBarrier barrier = new CyclicBarrier(THREADS_NUMBER);
        for (int i = 0; i < THREADS_NUMBER; ++i) {
            threadsList.add(new Thread(() -> {
                try {
                    barrier.await();
                } catch (Throwable t) {
                }
                results.add(intList.remove(numberInList));
            }));
        }
        for (Thread thread : threadsList) {
            thread.start();
        }
        for (Thread thread : threadsList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
        int amountOfTrues = 0;
        int amountOfFalses = 0;
        for (boolean res : results) {
            if (res) amountOfTrues++;
            else amountOfFalses++;
        }

        assertEquals(1, amountOfTrues);
        assertEquals(THREADS_NUMBER - 1, amountOfFalses);
    }

    @Test
    public void multithreadRemoveAndAppendTest() throws Exception {
        LockFreeList<Integer> intList = fillWithSerialIntegers(THREADS_NUMBER / 2);
        ArrayList<Thread> threadsList = new ArrayList<>();
        CyclicBarrier barrier = new CyclicBarrier(THREADS_NUMBER);
        for (int i = 0; i < THREADS_NUMBER / 2; ++i) {
            int number = i;
            threadsList.add(new Thread(() -> {
                try {
                    barrier.await();
                } catch (Throwable t) {
                }
                intList.append(number);
            }));
            threadsList.add(new Thread(() -> {
                try {
                    barrier.await();
                } catch (Throwable t) {
                }
                intList.remove(number);
            }));
        }
        for (Thread thread : threadsList) {
            thread.start();
        }
        for (Thread thread : threadsList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
        for (int i = 0; i < THREADS_NUMBER / 2; ++i) {
            assertTrue(intList.contains(i));
        }
    }

    @Test
    public void testContains() throws Exception {
        LockFreeList<Integer> intList = new LockFreeListImpl<>();
        intList.append(1);
        assertTrue(intList.contains(1));
        assertFalse(intList.contains(0));
    }
}