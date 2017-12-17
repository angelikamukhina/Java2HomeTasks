package messages.client_tracker.client;

import exceptions.UnableHandleQueryException;
import messages.Queries;
import messages.client_tracker.tracker.TrackerMessage;

import java.net.Socket;

public interface ClientMessage {
    TrackerMessage handleQuery(Socket clientSocket) throws UnableHandleQueryException;

    Queries.ClientTrackerQueries getQueryType();
}
