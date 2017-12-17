package messages.client_tracker.tracker;

import exceptions.UnableSendResponseException;
import utils.FileInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class ListResponse implements TrackerMessage {
    private Map<Integer, FileInfo> files;

    public ListResponse(Map<Integer, FileInfo> files) {
        this.files = files;
    }

    @Override
    public void send(DataOutputStream out) throws UnableSendResponseException {
        try {
            out.writeInt(files.size());
            for (Map.Entry<Integer, FileInfo> file : files.entrySet()) {
                out.writeInt(file.getKey());
                out.writeUTF(file.getValue().getName());
                out.writeLong(file.getValue().getSize());
            }
        } catch (IOException exception) {
            throw new UnableSendResponseException(exception);
        }
    }

    public Map<Integer, FileInfo> getFiles() {
        return files;
    }
}
