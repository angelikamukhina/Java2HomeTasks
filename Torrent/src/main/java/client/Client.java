package client;

import exceptions.UnableReadState;
import exceptions.UnableStoreState;
import utils.FileInfo;

import java.io.IOException;
import java.util.Map;

public interface Client {
    void start(String trackerHost, short seedPort, int threadsNumber) throws UnableReadState;
    void stop() throws UnableStoreState;
    Map<Integer, FileInfo> getAvailableFiles();
    int uploadFile(String filePath) throws IOException;
    boolean downloadFile(int fileId, String filePath) throws IOException;
}
