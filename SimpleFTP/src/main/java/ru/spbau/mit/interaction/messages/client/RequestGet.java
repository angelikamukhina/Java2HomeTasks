package ru.spbau.mit.interaction.messages.client;

import org.jetbrains.annotations.NotNull;
import ru.spbau.mit.interaction.ClientMessageType;

import java.io.Serializable;

public class RequestGet extends ClientMessage implements Serializable {
    @NotNull
    private final String filePath;

    public RequestGet(@NotNull String filePath) {
        this.filePath = filePath;
    }

    @NotNull
    public String getFilePath() {
        return filePath;
    }

    @NotNull
    @Override
    public ClientMessageType getType() {
        return ClientMessageType.REQUEST_GET;
    }
}
