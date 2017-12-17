package messages.client_tracker.client;

import exceptions.UnableHandleQueryException;
import messages.Queries;
import messages.client_tracker.tracker.UpdateResponse;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;

public class UpdateRequest implements ClientMessage {
    private static final byte QUERY_ID = 4;
    private final short port;
    private final Set<Integer> filesIds;

    public UpdateRequest(short port, Set<Integer> filesIds) {
        this.port = port;
        this.filesIds = filesIds;
    }

    @NotNull
    @Override
    public UpdateResponse handleQuery(@NotNull Socket clientSocket) throws UnableHandleQueryException {
        try {
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            out.writeByte(QUERY_ID);
            out.writeShort(port);
            out.writeInt(filesIds.size());
            for (Integer fileId : filesIds) {
                out.writeInt(fileId);
            }

            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            boolean status = in.readBoolean();
            return new UpdateResponse(status);
        } catch (IOException exception) {
            throw new UnableHandleQueryException(exception);
        }
    }

    public Set<Integer> getFilesIds() {
        return filesIds;
    }

    public short getPort() {
        return port;
    }

    @Override
    public Queries.ClientTrackerQueries getQueryType() {
        return Queries.ClientTrackerQueries.values()[QUERY_ID];
    }
}
