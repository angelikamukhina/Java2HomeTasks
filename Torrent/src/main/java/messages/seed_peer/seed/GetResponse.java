package messages.seed_peer.seed;

import exceptions.UnableSendResponseException;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;

public class GetResponse implements SeedMessage {
    private final byte[] partContent;

    public GetResponse(byte[] partContent) {
        this.partContent = partContent;
    }

    @Override
    public void send(@NotNull DataOutputStream out) throws UnableSendResponseException {
        try {
            out.write(partContent);
        } catch (IOException exception) {
            throw new UnableSendResponseException(exception);
        }
    }

    public byte[] getPartContent() {
        return partContent;
    }
}
