package tracker;

import messages.client_tracker.client.ClientMessage;
import messages.client_tracker.client.SourcesRequest;
import messages.client_tracker.client.UpdateRequest;
import messages.client_tracker.client.UploadRequest;
import messages.client_tracker.tracker.ListResponse;
import messages.client_tracker.tracker.SourcesResponse;
import messages.client_tracker.tracker.UpdateResponse;
import messages.client_tracker.tracker.UploadResponse;
import org.jetbrains.annotations.NotNull;
import utils.FileInfo;
import utils.IPv4;
import utils.SeedInfo;

import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ClientHandler implements Runnable {
    private final Socket socket;
    private final TrackerState trackerState;

    ClientHandler(Socket socket, TrackerState trackerState) {
        this.socket = socket;
        this.trackerState = trackerState;
    }

    @Override
    public void run() {
        ClientMessageHandler messageHandler = new ClientMessageHandler();
        ClientMessage message = messageHandler.getClientMessage(socket);
        boolean stopped = false;
        while (!stopped) {
            switch (message.getQueryType()) {
                case LIST:
                    executeList();
                    break;
                case UPDATE:
                    executeUpdate((UpdateRequest) message);
                    break;
                case UPLOAD:
                    executeUpload((UploadRequest) message);
                    break;
                case SOURCES:
                    executeSources((SourcesRequest) message);
                    break;
                case DISCONNECT:
                    stopped = true;
            }
            message = messageHandler.getClientMessage(socket);
        }
    }

    private void executeList() {
        Map<Integer, FileInfo> availableFiles = trackerState.getAvailableFiles();
        ListResponse listResponse = new ListResponse(availableFiles);
        ClientMessageHandler messageHandler = new ClientMessageHandler();
        messageHandler.sendMessage(socket, listResponse);
    }

    private void executeUpdate(@NotNull UpdateRequest message) {
        Set<Integer> filesIds = message.getFilesIds();
        short port = message.getPort();
        for (int fileId : filesIds) {
            trackerState.addNewSeedIfAbsent(fileId, new SeedInfo(
                    new IPv4(socket.getInetAddress().getAddress()), port));
        }
        UpdateResponse response = new UpdateResponse(true);
        ClientMessageHandler messageHandler = new ClientMessageHandler();
        messageHandler.sendMessage(socket, response);
    }

    private void executeUpload(@NotNull UploadRequest message) {
        int fileId = trackerState.addNewFile(message.getFileInfo());
        UploadResponse uploadResponse = new UploadResponse(fileId);
        ClientMessageHandler messageHandler = new ClientMessageHandler();
        messageHandler.sendMessage(socket, uploadResponse);
    }

    private void executeSources(@NotNull SourcesRequest message) {
        int fileId = message.getFileId();
        List<SeedInfo> seeds = trackerState.getSeeds(fileId);
        SourcesResponse sourcesResponse = new SourcesResponse(seeds);
        ClientMessageHandler messageHandler = new ClientMessageHandler();
        messageHandler.sendMessage(socket, sourcesResponse);
    }
}
