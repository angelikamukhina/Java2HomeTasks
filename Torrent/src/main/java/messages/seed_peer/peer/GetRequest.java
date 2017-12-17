package messages.seed_peer.peer;

import exceptions.UnableHandleQueryException;
import messages.Queries;
import messages.seed_peer.seed.GetResponse;
import utils.TorrentConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GetRequest implements PeerMessage {
    private static final byte QUERY_ID = 2;
    private final int fileId;
    private final int partNumber;

    public GetRequest(int fileId, int partNumber) {
        this.fileId = fileId;
        this.partNumber = partNumber;
    }

    @Override
    public GetResponse handleQuery(Socket peerSocket) throws UnableHandleQueryException {
        try {
            DataOutputStream out = new DataOutputStream(peerSocket.getOutputStream());
            out.writeByte(QUERY_ID);
            out.writeInt(fileId);
            out.writeInt(partNumber);

            DataInputStream in = new DataInputStream(peerSocket.getInputStream());
            byte[] partContent = new byte[TorrentConstants.PART_SIZE];
            in.read(partContent);
            return new GetResponse(partContent);
        } catch (IOException exception) {
            throw new UnableHandleQueryException(exception);
        }
    }

    @Override
    public Queries.SeedPeerQueries getType() {
        return Queries.SeedPeerQueries.GET;
    }

    public int getFileId() {
        return fileId;
    }

    public int getPartNumber() {
        return partNumber;
    }
}
