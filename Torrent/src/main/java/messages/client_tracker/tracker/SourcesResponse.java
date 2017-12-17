package messages.client_tracker.tracker;

import exceptions.UnableSendResponseException;
import org.jetbrains.annotations.NotNull;
import utils.SeedInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class SourcesResponse implements TrackerMessage {
    private final List<SeedInfo> seeds;

    public SourcesResponse(List<SeedInfo> seeds) {
        this.seeds = seeds;
    }

    @Override
    public void send(@NotNull DataOutputStream out) throws UnableSendResponseException {
        try {
            out.writeInt(seeds.size());
            for (SeedInfo seed : seeds) {
                seed.write(out);
            }
        } catch (IOException exception) {
            throw new UnableSendResponseException(exception);
        }
    }

    public List<SeedInfo> getSeeds() {
        return seeds;
    }
}
