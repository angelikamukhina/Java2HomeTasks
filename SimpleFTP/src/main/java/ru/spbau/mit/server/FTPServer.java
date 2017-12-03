package ru.spbau.mit.server;

import org.jetbrains.annotations.NotNull;
import ru.spbau.mit.exceptions.InternalServerException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FTPServer implements Server {
    private ServerSocket serverSocket;

    @Override
    public void start(int port, int threadsNumber, int fileBufferSize) throws InternalServerException {
        @NotNull ExecutorService threadPool = Executors.newFixedThreadPool(threadsNumber);
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create server socket", exception);
        }
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new ClientHandler(clientSocket, fileBufferSize));
            }
        } catch (SocketException ignored) {
        } catch (IOException exception) {
            throw new InternalServerException(
                    "Unable to set a connection with the client",
                    exception);
        }
    }

    @Override
    public void stop() throws InternalServerException {
        try {
            serverSocket.close();
        } catch (IOException exception) {
            throw new InternalServerException("Unable to close server socket", exception);
        }
    }
}