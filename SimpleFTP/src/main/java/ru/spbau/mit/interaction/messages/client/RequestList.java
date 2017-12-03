package ru.spbau.mit.interaction.messages.client;

import org.jetbrains.annotations.NotNull;
import ru.spbau.mit.interaction.ClientMessageType;

public class RequestList extends ClientMessage {
    @NotNull
    private final String pathToDirectory;

    public RequestList(@NotNull String pathToDirectory) {
        this.pathToDirectory = pathToDirectory;
    }

    @NotNull
    public String getPathToDirectory() {
        return pathToDirectory;
    }

    @NotNull
    @Override
    public ClientMessageType getType() {
        return ClientMessageType.REQUEST_LIST;
    }
}
