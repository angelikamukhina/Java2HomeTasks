package ru.spbau.mit.client;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;

public interface Client {
    void connect(@NotNull String host, int port);

    void disconnect();

    void executeList(@NotNull String pathToDirectory, @NotNull DataOutputStream streamToWriteTo);

    void executeGet(@NotNull String pathToFile, @NotNull DataOutputStream streamToWriteTo);
}
