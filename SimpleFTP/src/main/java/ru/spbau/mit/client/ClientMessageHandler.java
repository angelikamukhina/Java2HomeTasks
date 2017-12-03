package ru.spbau.mit.client;

import org.jetbrains.annotations.NotNull;
import ru.spbau.mit.exceptions.InternalClientException;
import ru.spbau.mit.exceptions.UnableGetMessageException;
import ru.spbau.mit.exceptions.UnableSendMessageException;
import ru.spbau.mit.interaction.messages.client.ClientMessage;
import ru.spbau.mit.interaction.messages.server.ServerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ClientMessageHandler {
    void sendMessage(@NotNull Socket socket, @NotNull ClientMessage message)
            throws UnableSendMessageException {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.reset();
            objectOutputStream.writeObject(message);
        } catch (IOException exception) {
            throw new UnableSendMessageException("The message can not be send", exception);
        }
    }

    @NotNull ServerMessage getMessage(@NotNull Socket socket)
            throws UnableGetMessageException, InternalClientException {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return (ServerMessage) objectInputStream.readObject();
        } catch (IOException exception) {
            throw new UnableGetMessageException("Unable to get message", exception);
        } catch (ClassNotFoundException exception) {
            throw new InternalClientException("There is no ServerMessage class", exception);
        }
    }
}
