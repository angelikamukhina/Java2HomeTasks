package messages.seed_peer.peer;

import exceptions.UnableHandleQueryException;
import messages.Queries;
import messages.seed_peer.seed.SeedMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DisconnectRequest implements PeerMessage {
    private static final byte QUERY_ID = 0;

    @Nullable
    @Override
    public SeedMessage handleQuery(@NotNull Socket clientSocket) throws UnableHandleQueryException {
        try {
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            out.writeByte(QUERY_ID);
        } catch (IOException exception) {
            throw new UnableHandleQueryException(exception);
        }
        return null;
    }

    @NotNull
    @Override
    public Queries.SeedPeerQueries getType() {
        return Queries.SeedPeerQueries.DISCONNECT;
    }
}
