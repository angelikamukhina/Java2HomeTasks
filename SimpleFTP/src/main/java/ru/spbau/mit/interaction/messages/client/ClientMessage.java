package ru.spbau.mit.interaction.messages.client;

import org.jetbrains.annotations.NotNull;
import ru.spbau.mit.interaction.ClientMessageType;

import java.io.Serializable;

public abstract class ClientMessage implements Serializable {
    @NotNull
    abstract public ClientMessageType getType();
}
