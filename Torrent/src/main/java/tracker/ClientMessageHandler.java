package tracker;

import exceptions.InternalTrackerClientException;
import exceptions.UnableParseQueryException;
import messages.Queries;
import messages.client_tracker.client.*;
import messages.client_tracker.tracker.TrackerMessage;
import org.jetbrains.annotations.NotNull;
import utils.FileInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

class ClientMessageHandler {
    @NotNull ClientMessage getClientMessage(@NotNull Socket socket) throws UnableParseQueryException {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            byte queryId = in.readByte();
            Queries.ClientTrackerQueries queryType = Queries.ClientTrackerQueries.values()[queryId];
            switch (queryType) {
                case LIST:
                    return getListRequest();
                case UPDATE:
                    return getUpdateRequest(in);
                case UPLOAD:
                    return getUploadRequest(in);
                case SOURCES:
                    return getSourcesRequest(in);
                case DISCONNECT:
                    return getDisconnectRequest();
                default:
                    throw new UnableParseQueryException("Wrong client query type: " + queryId);
            }
        } catch (IOException exception) {
            throw new UnableParseQueryException(exception);
        }
    }

    void sendMessage(@NotNull Socket socket, @NotNull TrackerMessage message) throws InternalTrackerClientException {
        DataOutputStream out;
        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException exception) {
            throw new InternalTrackerClientException(exception);
        }
        message.send(out);
    }

    private ListRequest getListRequest() {
        return new ListRequest();
    }

    private UploadRequest getUploadRequest(@NotNull DataInputStream in) throws UnableParseQueryException {
        try {
            String fileName = in.readUTF();
            long size = in.readLong();
            return new UploadRequest(new FileInfo(fileName, size));
        } catch (IOException exception) {
            throw new UnableParseQueryException(exception);
        }
    }

    private UpdateRequest getUpdateRequest(@NotNull DataInputStream in) throws UnableParseQueryException {
        try {
            short port = in.readShort();
            int count = in.readInt();
            Set<Integer> ids = new HashSet<>();
            for (int idNum = 0; idNum < count; ++idNum) {
                int id = in.readInt();
                ids.add(id);
            }
            return new UpdateRequest(port, ids);
        } catch (IOException exception) {
            throw new UnableParseQueryException(exception);
        }
    }

    private SourcesRequest getSourcesRequest(@NotNull DataInputStream in) throws UnableParseQueryException {
        try {
            int fileId = in.readInt();
            return new SourcesRequest(fileId);
        } catch (IOException exception) {
            throw new UnableParseQueryException(exception);
        }
    }

    private DisconnectRequest getDisconnectRequest() {
        return new DisconnectRequest();
    }
}
