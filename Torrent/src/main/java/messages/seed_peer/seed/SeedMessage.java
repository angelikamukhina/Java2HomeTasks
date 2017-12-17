package messages.seed_peer.seed;

import exceptions.UnableSendResponseException;

import java.io.DataOutputStream;

public interface SeedMessage {
    void send(DataOutputStream out) throws UnableSendResponseException;
}
