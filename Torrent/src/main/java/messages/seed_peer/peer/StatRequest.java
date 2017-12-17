package messages.seed_peer.peer;

import exceptions.UnableHandleQueryException;
import exceptions.UnableSendResponseException;
import messages.Queries;
import messages.seed_peer.seed.StatResponse;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.BitSet;

public class StatRequest implements PeerMessage {
    private static final byte QUERY_ID = 1;
    private final int fileId;

    public StatRequest(int fileId) {
        this.fileId = fileId;
    }

    @NotNull
    @Override
    public StatResponse handleQuery(@NotNull Socket peerSocket) throws UnableHandleQueryException {
        try {
            DataOutputStream out = new DataOutputStream(peerSocket.getOutputStream());
            out.writeByte(QUERY_ID);
            out.writeInt(fileId);

            DataInputStream in = new DataInputStream(peerSocket.getInputStream());
            int count = in.readInt();
            BitSet parts = new BitSet();
            for (int part = 0; part < count; ++part) {
                int partNumber = in.readInt();
                parts.set(partNumber);
            }
            return new StatResponse(parts);
        } catch (IOException exception) {
            throw new UnableSendResponseException(exception);
        }
    }

    @NotNull
    @Override
    public Queries.SeedPeerQueries getType() {
        return Queries.SeedPeerQueries.STAT;
    }

    public int getFileId() {
        return fileId;
    }
}
