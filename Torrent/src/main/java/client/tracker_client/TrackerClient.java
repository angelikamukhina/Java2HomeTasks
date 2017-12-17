package client.tracker_client;

import exceptions.InternalTrackerClientException;
import exceptions.UnableSetConnectionException;
import utils.FileInfo;
import utils.SeedInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TrackerClient {
    void start(String trackerHost) throws UnableSetConnectionException;
    void stop() throws InternalTrackerClientException;
    Map<Integer, FileInfo> getFilesList();
    int uploadNewFile(String pathToFile) throws InternalTrackerClientException;
    List<SeedInfo> getSeeds(int fileId);
    boolean updateClientInfo(short seedPort, Set<Integer> ids);
}
