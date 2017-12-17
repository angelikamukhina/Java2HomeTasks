package client.tracker_client;

import exceptions.InternalTrackerClientException;
import exceptions.UnableSetConnectionException;
import messages.client_tracker.client.ListRequest;
import messages.client_tracker.client.SourcesRequest;
import messages.client_tracker.client.UpdateRequest;
import messages.client_tracker.client.UploadRequest;
import messages.client_tracker.tracker.ListResponse;
import messages.client_tracker.tracker.SourcesResponse;
import messages.client_tracker.tracker.UploadResponse;
import org.jetbrains.annotations.NotNull;
import utils.FileInfo;
import utils.SeedInfo;
import utils.TorrentConstants;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TrackerClientImpl implements TrackerClient {
    private Socket socket;

    @Override
    public void start(String trackerHost) throws UnableSetConnectionException {
        try {
            socket = new Socket(trackerHost, TorrentConstants.TRACKER_PORT);
        } catch (IOException exception) {
            throw new UnableSetConnectionException(exception);
        }
    }

    @Override
    public void stop() throws InternalTrackerClientException {
        try {
            socket.close();
        } catch (IOException exception) {
            throw new InternalTrackerClientException(exception);
        }
    }

    @Override
    public Map<Integer, FileInfo> getFilesList() {
        ListRequest query = new ListRequest();
        ListResponse response = query.handleQuery(socket);
        return response.getFiles();
    }

    @Override
    public int uploadNewFile(@NotNull String filePath) throws InternalTrackerClientException {
        File file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            String fileName = file.getName();
            long size = file.length();
            UploadRequest query = new UploadRequest(new FileInfo(fileName, size));
            UploadResponse response = query.handleQuery(socket);
            return response.getFileId();
        } else {
            throw new InternalTrackerClientException("The file " + filePath + "doesn't exist");
        }
    }

    @Override
    public List<SeedInfo> getSeeds(int fileId) {
        SourcesRequest query = new SourcesRequest(fileId);
        SourcesResponse response = query.handleQuery(socket);
        return response.getSeeds();
    }

    @Override
    public void updateClientInfo(short seedPort, Set<Integer> filesIds) {
        UpdateRequest query = new UpdateRequest(seedPort, filesIds);
        query.handleQuery(socket);
    }
}
