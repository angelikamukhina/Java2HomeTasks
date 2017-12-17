package messages.client_tracker.client;

import exceptions.UnableHandleQueryException;
import messages.Queries;
import messages.client_tracker.tracker.TrackerMessage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DisconnectRequest implements ClientMessage {
    private static final byte QUERY_ID = 0;
    @Override
    public TrackerMessage handleQuery(Socket clientSocket) throws UnableHandleQueryException {
        DataOutputStream out;
        try {
            out = new DataOutputStream(clientSocket.getOutputStream());
            out.writeByte(QUERY_ID);
        } catch (IOException exception) {
            throw new UnableHandleQueryException(exception);
        }
        return null;
    }

    @Override
    public Queries.ClientTrackerQueries getQueryType() {
        return Queries.ClientTrackerQueries.DISCONNECT;
    }
}
