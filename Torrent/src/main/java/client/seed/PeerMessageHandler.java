package client.seed;

import exceptions.InternalSeedException;
import exceptions.UnableParseQueryException;
import messages.Queries;
import messages.seed_peer.peer.DisconnectRequest;
import messages.seed_peer.peer.GetRequest;
import messages.seed_peer.peer.PeerMessage;
import messages.seed_peer.peer.StatRequest;
import messages.seed_peer.seed.SeedMessage;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class PeerMessageHandler {
    @NotNull PeerMessage getPeerMessage(@NotNull Socket socket) throws UnableParseQueryException {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            byte queryId = in.readByte();
            Queries.SeedPeerQueries messageType = Queries.SeedPeerQueries.values()[queryId];
            switch (messageType) {
                case GET:
                    return getGetRequestMessage(in);
                case STAT:
                    return getStatRequestMessage(in);
                case DISCONNECT:
                    return new DisconnectRequest();
                default:
                    throw new UnableParseQueryException("Wrong peer query type " + queryId);
            }
        } catch (IOException exception) {
            throw new UnableParseQueryException(exception);
        }
    }

    void sendMessage(@NotNull Socket socket, @NotNull SeedMessage message) throws InternalSeedException {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            message.send(out);
        } catch (IOException exception) {
            throw new InternalSeedException(exception);
        }
    }

    private GetRequest getGetRequestMessage(@NotNull DataInputStream in) throws UnableParseQueryException {
        try {
            int fileId = in.readInt();
            int partNumber = in.readInt();
            return new GetRequest(fileId, partNumber);
        } catch (IOException exception) {
            throw new UnableParseQueryException(exception);
        }
    }

    private StatRequest getStatRequestMessage(@NotNull DataInputStream in) throws UnableParseQueryException {
        try {
            int fileId = in.readInt();
            return new StatRequest(fileId);
        } catch (IOException exception) {
            throw new UnableParseQueryException((exception));
        }
    }
}
