package messages.seed_peer.peer;

import exceptions.UnableHandleQueryException;
import messages.Queries;
import messages.seed_peer.seed.SeedMessage;

import java.net.Socket;

public interface PeerMessage {
    SeedMessage handleQuery(Socket peerSocket) throws UnableHandleQueryException;

    Queries.SeedPeerQueries getType();
}
