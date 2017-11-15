package ru.spbau.mit;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.Socket;

public class Client {
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public void connect(@NotNull String host, int port) {
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to set a connection with the server");
        }
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create streams from socket: client");
        }
    }

    public void disconnect() throws IOException {
        outputStream.writeInt(0);
        outputStream.close();
        socket.close();
    }

    public void executeList(@NotNull String pathToDirectory, @NotNull Writer writer) {
        try {
            outputStream.writeInt(1);
            outputStream.writeUTF(pathToDirectory);
            int size = inputStream.readInt();
            if (size == 0) {
                writer.write("There is no such directory: " + pathToDirectory);
            }
            for (int fileIdx = 0; fileIdx < size; fileIdx++) {
                final @NotNull String fileName = inputStream.readUTF();
                boolean isDirectory = inputStream.readBoolean();
                writer.write(fileName + ": " + isDirectory + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeGet(@NotNull String pathToFile, @NotNull Writer writer) throws IOException {
        outputStream.writeInt(2);
        outputStream.writeUTF(pathToFile);

        long fileSize = inputStream.readLong();
        final @NotNull byte[] fileContent = new byte[(int) fileSize];
        int bytesRead = inputStream.read(fileContent);
        if (bytesRead != fileSize) {
            System.out.println("Only " + bytesRead + "bytes from "
                    + fileSize + "of file content were received");
        }
        writer.write(new String(fileContent));
    }
}