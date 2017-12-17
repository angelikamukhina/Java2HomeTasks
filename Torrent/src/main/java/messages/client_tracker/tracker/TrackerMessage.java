package messages.client_tracker.tracker;

import exceptions.UnableSendResponseException;

import java.io.DataOutputStream;

public interface TrackerMessage {
    void send(DataOutputStream out) throws UnableSendResponseException;
}
