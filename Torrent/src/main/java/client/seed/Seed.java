package client.seed;

import client.ClientState;
import exceptions.InternalSeedException;

public interface Seed {
    void start(short port, int threadsNumber, ClientState clientState) throws InternalSeedException;

    void stop() throws InternalSeedException;

    short getPort();
}
