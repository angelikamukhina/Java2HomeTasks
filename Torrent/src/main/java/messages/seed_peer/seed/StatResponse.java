package messages.seed_peer.seed;

import exceptions.UnableSendResponseException;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

public class StatResponse implements SeedMessage {
    private final BitSet parts;

    public StatResponse(BitSet parts) {
        this.parts = parts;
    }

    @Override
    public void send(@NotNull DataOutputStream out) throws UnableSendResponseException {
        try {
            out.writeInt(parts.cardinality());
            for (int partNumber = 0; partNumber < parts.length(); ++partNumber) {
                if (parts.get(partNumber)) {
                    out.writeInt(partNumber);
                }
            }
        } catch (IOException exception) {
            throw new UnableSendResponseException(exception);
        }
    }

    public BitSet getParts() {
        return parts;
    }
}
