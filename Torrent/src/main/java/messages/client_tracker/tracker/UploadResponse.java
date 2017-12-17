package messages.client_tracker.tracker;

import exceptions.UnableSendResponseException;
import java.io.DataOutputStream;
import java.io.IOException;

public class UploadResponse implements TrackerMessage {
    private final int fileId;

    public UploadResponse(int fileId) {
        this.fileId = fileId;
    }

    @Override
    public void send(DataOutputStream out) throws UnableSendResponseException {
        try {
            out.writeInt(fileId);
        } catch (IOException exception) {
            throw new UnableSendResponseException(exception);
        }
    }

    public int getFileId() {
        return fileId;
    }
}
