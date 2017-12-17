package messages.client_tracker.client;

import exceptions.UnableHandleQueryException;
import messages.Queries;
import messages.client_tracker.tracker.SourcesResponse;
import org.jetbrains.annotations.NotNull;
import utils.IPv4;
import utils.SeedInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SourcesRequest implements ClientMessage {
    private final static byte QUERY_ID = 3;
    private final int fileId;

    public SourcesRequest(int fileId) {
        this.fileId = fileId;
    }

    @NotNull
    @Override
    public SourcesResponse handleQuery(@NotNull Socket clientSocket) throws UnableHandleQueryException {
        try {
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            out.writeByte(QUERY_ID);
            out.writeInt(fileId);
            List<SeedInfo> seeds = new ArrayList<>();
            int seedsCount = in.readInt();
            for (int seed = 0; seed < seedsCount; ++seed) {
                IPv4 ip = IPv4.getIP(in);
                short port = in.readShort();
                seeds.add(new SeedInfo(ip, port));
            }
            return new SourcesResponse(seeds);
        } catch (IOException exception) {
            throw new UnableHandleQueryException(exception);
        }
    }

    @Override
    public Queries.ClientTrackerQueries getQueryType() {
        return Queries.ClientTrackerQueries.values()[QUERY_ID];
    }

    public int getFileId() {
        return fileId;
    }
}
