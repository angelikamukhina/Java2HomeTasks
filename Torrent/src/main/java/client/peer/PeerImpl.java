package client.peer;

import exceptions.InternalPeerException;
import exceptions.UnableSetConnectionException;
import messages.seed_peer.peer.DisconnectRequest;
import messages.seed_peer.peer.GetRequest;
import messages.seed_peer.peer.StatRequest;
import messages.seed_peer.seed.GetResponse;
import messages.seed_peer.seed.StatResponse;
import org.jetbrains.annotations.NotNull;
import utils.SeedInfo;

import java.io.IOException;
import java.net.Socket;
import java.util.BitSet;

public class PeerImpl implements Peer {
    private Socket socket;

    @Override
    public void start(@NotNull SeedInfo seedInfo) throws UnableSetConnectionException {
        try {
            socket = new Socket(seedInfo.getIp().getInetAddress(), seedInfo.getPort());
        } catch (IOException exception) {
            throw new UnableSetConnectionException(exception);
        }
    }

    @Override
    public void stop() throws InternalPeerException {
        if (socket != null) {
            DisconnectRequest disconnectRequest = new DisconnectRequest();
            disconnectRequest.handleQuery(socket);
            try {
                socket.close();
            } catch (IOException exception) {
                throw new InternalPeerException(exception);
            }
        }
    }

    @Override
    public BitSet getAvailablePartsInfo(int fileId) {
        StatRequest statRequest = new StatRequest(fileId);
        StatResponse response = statRequest.handleQuery(socket);
        return response.getParts();
    }

    @Override
    public byte[] getPart(int fileId, int partNumber) {
        GetRequest getRequest = new GetRequest(fileId, partNumber);
        GetResponse response = getRequest.handleQuery(socket);
        return response.getPartContent();
    }
}
