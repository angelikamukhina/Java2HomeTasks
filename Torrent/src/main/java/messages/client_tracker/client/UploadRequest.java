package messages.client_tracker.client;

import exceptions.UnableHandleQueryException;
import messages.Queries;
import messages.client_tracker.tracker.UploadResponse;
import org.jetbrains.annotations.NotNull;
import utils.FileInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UploadRequest implements ClientMessage {
    private static final int QUERY_ID = 2;
    private final FileInfo fileInfo;

    public UploadRequest(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    @NotNull
    @Override
    public UploadResponse handleQuery(@NotNull Socket clientSocket) throws UnableHandleQueryException {
        try {
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            out.writeByte(QUERY_ID);
            out.writeUTF(fileInfo.getName());
            out.writeLong(fileInfo.getSize());
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            int fileId = in.readInt();
            return new UploadResponse(fileId);
        } catch (IOException exception) {
            throw new UnableHandleQueryException(exception);
        }
    }

    @Override
    public Queries.ClientTrackerQueries getQueryType() {
        return Queries.ClientTrackerQueries.values()[QUERY_ID];
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }
}
