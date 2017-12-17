package tracker;

import exceptions.InternalTrackerException;
import exceptions.UnableReadState;
import exceptions.UnableStoreState;
import org.jetbrains.annotations.NotNull;
import utils.TorrentConstants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackerImpl implements Tracker {
    @NotNull
    private final TrackerState trackerState = new TrackerState();
    private ServerSocket serverSocket;

    @Override
    public void start(int threadsNumber) throws UnableReadState, InternalTrackerException {
        try {
            trackerState.getFromFile(TorrentConstants.trackerStateFile);
        } catch (IOException exception) {
            throw new UnableReadState(exception);
        }
        try {
            serverSocket = new ServerSocket(TorrentConstants.TRACKER_PORT);
            ExecutorService threadPool = Executors.newFixedThreadPool(threadsNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new ClientHandler(clientSocket, trackerState));
            }
        } catch (SocketException ignored) {
        } catch (IOException exception) {
            throw new InternalTrackerException(exception);
        }
    }

    @Override
    public void stop() throws InternalTrackerException, UnableStoreState {
        try {
            trackerState.storeToFile(TorrentConstants.trackerStateFile);
        } catch (IOException exception) {
            throw new UnableStoreState(exception);
        }
        try {
            serverSocket.close();
        } catch (IOException exception) {
            throw new InternalTrackerException(exception);
        }
    }
}
