package messages.client_tracker.client;

import exceptions.UnableHandleQueryException;
import messages.Queries;
import messages.client_tracker.tracker.ListResponse;
import org.jetbrains.annotations.NotNull;
import utils.FileInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ListRequest implements ClientMessage {
    private static final byte QUERY_ID = 1;

    @NotNull
    @Override
    public ListResponse handleQuery(@NotNull Socket clientSocket) throws UnableHandleQueryException {
        try {
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            out.writeByte(QUERY_ID);
            int count = in.readInt();
            Map<Integer, FileInfo> filesList = new HashMap<>();
            for (int fileCount = 0; fileCount < count; ++fileCount) {
                int fileId = in.readInt();
                String name = in.readUTF();
                long size = in.readLong();
                filesList.put(fileId, new FileInfo(name, size));
            }
            return new ListResponse(filesList);
        } catch (IOException exception) {
            throw new UnableHandleQueryException(exception);
        }
    }

    @Override
    public Queries.ClientTrackerQueries getQueryType() {
        return Queries.ClientTrackerQueries.values()[QUERY_ID];
    }
}
