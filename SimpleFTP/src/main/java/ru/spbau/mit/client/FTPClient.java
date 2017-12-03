package ru.spbau.mit.client;

import org.jetbrains.annotations.NotNull;
import ru.spbau.mit.interaction.messages.client.ClientMessage;
import ru.spbau.mit.interaction.messages.client.RequestDisconnect;
import ru.spbau.mit.interaction.messages.client.RequestGet;
import ru.spbau.mit.interaction.messages.client.RequestList;
import ru.spbau.mit.interaction.messages.server.ResponseGet;
import ru.spbau.mit.interaction.messages.server.ResponseList;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class FTPClient implements Client {
    private final ClientMessageHandler messageHandler = new ClientMessageHandler();
    private Socket socket;

    @Override
    public void connect(@NotNull String host, int port) {
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to set a connection with the server");
        }
    }

    @Override
    public void disconnect() {
        ClientMessage message = new RequestDisconnect();
        messageHandler.sendMessage(socket, message);
        try {
            socket.close();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to close the socket", exception);
        }
    }

    @Override
    public void executeList(
            @NotNull String pathToDirectory,
            @NotNull DataOutputStream streamToWriteTo) {
        ClientMessage clientMessage = new RequestList(pathToDirectory);
        messageHandler.sendMessage(socket, clientMessage);
        ResponseList message = (ResponseList) messageHandler.getMessage(socket);
        int filesNumber = message.getFilesNumber();
        try {
            streamToWriteTo.writeInt(filesNumber);
            if (message.isDirectoryValid()) {
                List<ResponseList.FileInfo> filesPaths = message.getFilesPaths();
                for (ResponseList.FileInfo filePath : filesPaths) {
                    streamToWriteTo.writeUTF(filePath.getFilePath());
                    streamToWriteTo.writeBoolean(filePath.getIsDirectory());
                }
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "Unable to write to the given DataOutputStream object",
                    exception);
        }
    }

    @Override
    public void executeGet(
            @NotNull String pathToFile,
            @NotNull DataOutputStream streamToWriteTo) {
        messageHandler.sendMessage(socket, new RequestGet(pathToFile));
        long fileSize;
        long bytesSent;
        boolean theFirstServerMessage = true;
        do {
            ResponseGet serverMessage = (ResponseGet) messageHandler.getMessage(socket);
            fileSize = serverMessage.getFileSize();
            try {
                if (theFirstServerMessage) {
                    streamToWriteTo.writeLong(fileSize);
                    if (serverMessage.isFileValid()) {
                        break;
                    }
                    theFirstServerMessage = false;
                }
                bytesSent = serverMessage.getBytesSent();
                streamToWriteTo.write(serverMessage.getPartOfFileContent());
                streamToWriteTo.flush();
            } catch (IOException exception) {
                throw new IllegalArgumentException(
                        "Unable to write to the given DataOutputStream object",
                        exception);
            }
        } while (bytesSent < fileSize);
    }
}