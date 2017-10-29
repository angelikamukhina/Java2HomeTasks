package ru.spbau.mit.java;

public interface LockFreeList<T> {
    boolean isEmpty();

    /**
     * Appends value to the end of list
     */
    void append(T value);

    boolean remove(T value);

    boolean contains(T value);
}
