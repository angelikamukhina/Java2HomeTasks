package ru.spbau.mit.server;

import org.jetbrains.annotations.NotNull;
import ru.spbau.mit.exceptions.InternalServerException;
import ru.spbau.mit.exceptions.UnableGetMessageException;
import ru.spbau.mit.exceptions.UnableSendMessageException;
import ru.spbau.mit.interaction.messages.client.ClientMessage;
import ru.spbau.mit.interaction.messages.server.ServerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ServerMessageHandler {
    void sendMessage(@NotNull Socket clientSocket, ServerMessage message)
            throws UnableSendMessageException {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutputStream.reset();
            objectOutputStream.writeObject(message);
        } catch (IOException exception) {
            throw new UnableSendMessageException("The message can not be send", exception);
        }
    }

    @NotNull ClientMessage getMessage(@NotNull Socket clientSocket)
            throws UnableGetMessageException, InternalServerException {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            return (ClientMessage) objectInputStream.readObject();
        } catch (IOException exception) {
            throw new UnableGetMessageException(
                    "Unable to get the message from the server",
                    exception);
        } catch (ClassNotFoundException exception) {
            throw new InternalServerException("There is no ClientMessage class", exception);
        }
    }
}
