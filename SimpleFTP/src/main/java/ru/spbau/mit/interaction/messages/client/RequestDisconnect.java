package ru.spbau.mit.interaction.messages.client;

import org.jetbrains.annotations.NotNull;
import ru.spbau.mit.interaction.ClientMessageType;

public class RequestDisconnect extends ClientMessage {
    @NotNull
    @Override
    public ClientMessageType getType() {
        return ClientMessageType.REQUEST_DISCONNECT;
    }
}
