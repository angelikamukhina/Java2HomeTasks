package ru.spbau.mit.server;

import org.jetbrains.annotations.NotNull;
import ru.spbau.mit.interaction.ClientMessageType;
import ru.spbau.mit.interaction.messages.client.ClientMessage;
import ru.spbau.mit.interaction.messages.client.RequestGet;
import ru.spbau.mit.interaction.messages.client.RequestList;
import ru.spbau.mit.interaction.messages.server.ResponseGet;
import ru.spbau.mit.interaction.messages.server.ResponseList;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ClientHandler implements Runnable {
    private final int fileBufferSize;
    @NotNull
    private final ServerMessageHandler messageHandler = new ServerMessageHandler();
    @NotNull
    private final Socket clientSocket;

    ClientHandler(@NotNull Socket clientSocket, int fileBufferSize) {
        this.fileBufferSize = fileBufferSize;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        boolean stopped = false;
        while (!stopped) {
            ClientMessage clientMessage = messageHandler.getMessage(clientSocket);
            ClientMessageType message = clientMessage.getType();
            switch (message) {
                case REQUEST_LIST:
                    executeList((RequestList) clientMessage);
                    break;
                case REQUEST_GET:
                    executeGet((RequestGet) clientMessage);
                    break;
                case REQUEST_DISCONNECT:
                    stopped = true;
                    break;
                default:
                    throw new IllegalStateException("Wrong client message type");
            }
        }
    }

    private void executeList(@NotNull RequestList message) {
        final @NotNull Path directoryPath = Paths.get(message.getPathToDirectory());
        final @NotNull File directory = directoryPath.toFile();
        final @NotNull ResponseList serverMessage = new ResponseList();
        if (!directory.exists() || !directory.isDirectory()) {
            serverMessage.setInvalidDirectory();
        } else {
            final File[] files = directory.listFiles();
            if (files != null) {
                List<ResponseList.FileInfo> paths = Arrays.stream(files)
                        .map(file -> new ResponseList.FileInfo(
                                file.getPath(), file.isDirectory())
                        )
                        .collect(Collectors.toList());
                serverMessage.setFilesPaths(paths);
            } else {
                serverMessage.setInvalidDirectory();
            }
        }
        messageHandler.sendMessage(clientSocket, serverMessage);
    }

    private void executeGet(@NotNull RequestGet message) {
        final @NotNull File file = Paths.get(message.getFilePath()).toFile();
        final @NotNull ResponseGet serverMessage = new ResponseGet(file.length());
        if (!file.exists()) {
            serverMessage.setInvalidFile();
        } else {
            try (FileInputStream fileInStream = new FileInputStream(file)) {
                BufferedInputStream in = new BufferedInputStream(fileInStream);
                byte[] fileBuffer = new byte[fileBufferSize];
                serverMessage.setFileSize(file.length());
                int bytesRead = in.read(fileBuffer);
                while (bytesRead != -1) {
                    serverMessage.setPartOfFileContent(fileBuffer, bytesRead);
                    messageHandler.sendMessage(clientSocket, serverMessage);
                    bytesRead = in.read(fileBuffer);
                }
            } catch (IOException exception) {
                serverMessage.setInvalidFile();
            }
        }
    }
}
