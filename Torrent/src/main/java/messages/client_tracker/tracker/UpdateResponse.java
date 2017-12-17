package messages.client_tracker.tracker;

import exceptions.UnableSendResponseException;

import java.io.DataOutputStream;
import java.io.IOException;

public class UpdateResponse implements TrackerMessage {
    private final boolean status;

    public UpdateResponse(boolean status) {
        this.status = status;
    }

    @Override
    public void send(DataOutputStream out) throws UnableSendResponseException {
        try {
            out.writeBoolean(status);
        } catch (IOException exception) {
            throw new UnableSendResponseException(exception);
        }
    }

    public boolean getStatus() {
        return status;
    }
}
