package client;

import client.peer.Peer;
import client.peer.PeerImpl;
import client.seed.Seed;
import client.seed.SeedImpl;
import client.tracker_client.TrackerClient;
import client.tracker_client.TrackerClientImpl;
import exceptions.UnableReadState;
import exceptions.UnableStoreState;
import utils.FileInfo;
import utils.SeedInfo;
import utils.TorrentConstants;

import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

public class ClientImpl implements Client {
    private ClientState state = new ClientState();
    private Peer peer = new PeerImpl();
    private Seed seed = new SeedImpl();
    private Thread seedThread;
    private TrackerClient trackerClient = new TrackerClientImpl();

    @Override
    public void start(String trackerHost, short seedPort, int threadsNumber)
            throws UnableReadState {
        try {
            state.getFromFile(TorrentConstants.clientStateFile);
        } catch (IOException exception) {
            throw new UnableReadState(exception);
        }
        trackerClient.start(trackerHost);
        seedThread = new Thread(() -> seed.start(seedPort, threadsNumber, state));
        seedThread.start();
        trackerClient.updateClientInfo(seedPort, state.getAvailableFilesIds());
    }

    @Override
    public void stop() throws UnableStoreState {
        peer.stop();
        seed.stop();
        trackerClient.stop();
        try {
            state.storeToFile(TorrentConstants.clientStateFile);
        } catch (IOException exception) {
            throw new UnableStoreState(exception);
        }
    }

    @Override
    public Map<Integer, FileInfo> getAvailableFiles() {
        Map<Integer, FileInfo> files = trackerClient.getFilesList();
        state.updateAvailableForDownloadingFiles(files);
        return files;
    }

    @Override
    public int uploadFile(String filePath) throws IOException {
        int fileId = trackerClient.uploadNewFile(filePath);
        state.addWholeFile(fileId, filePath);
        trackerClient.updateClientInfo(seed.getPort(), state.getAvailableFilesIds());
        return fileId;
    }

    @Override
    public boolean downloadFile(int fileId, String filePath) throws IOException {
        trackerClient.getFilesList();
        List<SeedInfo> seeds = trackerClient.getSeeds(fileId);
        boolean theFirstPartGot = false;
        for (SeedInfo seedInfo : seeds) {
            peer.start(seedInfo);
            BitSet parts = peer.getAvailablePartsInfo(fileId);
            BitSet peerParts = state.getParts(fileId);
            for (int part = 0; part < parts.length(); ++part) {
                if (parts.get(part) && !peerParts.get(part)) {
                    byte[] partContent = peer.getPart(fileId, part);
                    state.addPartContent(fileId, part, partContent);
                    if (!theFirstPartGot) {
                        trackerClient.updateClientInfo(seed.getPort(), state.getAvailableFilesIds());
                    }
                    theFirstPartGot = true;
                }
            }
        }
        state.storePartsToFile(fileId, filePath);
        return true;
    }
}
