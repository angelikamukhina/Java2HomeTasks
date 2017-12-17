package messages.client_tracker.tracker;

import exceptions.UnableSendResponseException;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;

public class UploadResponse implements TrackerMessage {
    private final int fileId;

    public UploadResponse(int fileId) {
        this.fileId = fileId;
    }

    @Override
    public void send(@NotNull DataOutputStream out) throws UnableSendResponseException {
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
