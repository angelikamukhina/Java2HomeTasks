package client.peer;

import exceptions.InternalPeerException;
import exceptions.UnableSetConnectionException;
import utils.SeedInfo;

import java.util.BitSet;

public interface Peer {
    void start(SeedInfo seedInfo) throws UnableSetConnectionException;

    void stop() throws InternalPeerException;

    BitSet getAvailablePartsInfo(int fileId);

    byte[] getPart(int fileId, int partNumber);
}
