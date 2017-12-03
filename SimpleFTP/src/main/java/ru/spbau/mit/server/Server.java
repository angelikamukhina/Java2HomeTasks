package ru.spbau.mit.server;

import ru.spbau.mit.exceptions.InternalServerException;

public interface Server {
    void start(int port, int threadsNumber, int fileBufferSize) throws InternalServerException;

    void stop() throws InternalServerException;
}
