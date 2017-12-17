package messages.client_tracker.tracker;

import exceptions.UnableSendResponseException;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;

public class UpdateResponse implements TrackerMessage {
    private final boolean status;

    public UpdateResponse(boolean status) {
        this.status = status;
    }

    @Override
    public void send(@NotNull DataOutputStream out) throws UnableSendResponseException {
        try {
            out.writeBoolean(status);
        } catch (IOException exception) {
            throw new UnableSendResponseException(exception);
        }
    }
}
