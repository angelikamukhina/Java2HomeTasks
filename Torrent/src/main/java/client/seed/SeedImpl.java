package client.seed;

import client.ClientState;
import exceptions.InternalSeedException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SeedImpl implements Seed {
    private ServerSocket socket;

    @Override
    public void start(short port, int threadsNumber, ClientState clientState)
            throws InternalSeedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadsNumber);
        try {
            socket = new ServerSocket(port);
            while (true) {
                Socket peerSocket = socket.accept();
                threadPool.submit(new PeerHandler(peerSocket, clientState));
            }
        } catch (SocketException ignored) {
        } catch (IOException exception) {
            throw new InternalSeedException(exception);
        }
    }

    @Override
    public void stop() throws InternalSeedException {
        try {
            socket.close();
        } catch (IOException exception) {
            throw new InternalSeedException(exception);
        }
    }

    @Override
    public short getPort() {
        return (short) socket.getLocalPort();
    }
}
