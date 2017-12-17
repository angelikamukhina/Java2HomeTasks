package client.seed;

import client.ClientState;
import messages.seed_peer.peer.GetRequest;
import messages.seed_peer.peer.PeerMessage;
import messages.seed_peer.peer.StatRequest;
import messages.seed_peer.seed.GetResponse;
import messages.seed_peer.seed.StatResponse;

import java.net.Socket;
import java.util.BitSet;

class PeerHandler implements Runnable {
    private final Socket peerSocket;
    private final ClientState clientState;

    PeerHandler(Socket peerSocket, ClientState clientState) {
        this.peerSocket = peerSocket;
        this.clientState = clientState;
    }

    @Override
    public void run() {
        PeerMessageHandler messageHandler = new PeerMessageHandler();
        boolean stopped = false;
        PeerMessage message = messageHandler.getPeerMessage(peerSocket);
        while (!stopped) {
            switch (message.getType()) {
                case GET:
                    executeGet((GetRequest)message);
                    break;
                case STAT:
                    executeStat((StatRequest)message);
                    break;
                case DISCONNECT:
                    stopped = true;
                    break;
            }
            message = messageHandler.getPeerMessage(peerSocket);
        }
    }

    private void executeStat(StatRequest message) {
        int fileId = message.getFileId();
        BitSet parts = clientState.getParts(fileId);
        StatResponse response = new StatResponse(parts);
        PeerMessageHandler messageHandler = new PeerMessageHandler();
        messageHandler.sendMessage(peerSocket, response);
    }

    private void executeGet(GetRequest message) {
        int fileId = message.getFileId();
        int partNumber = message.getPartNumber();
        GetResponse response = new GetResponse(clientState.getPartContent(fileId, partNumber));
        PeerMessageHandler messageHandler = new PeerMessageHandler();
        messageHandler.sendMessage(peerSocket, response);
    }
}
