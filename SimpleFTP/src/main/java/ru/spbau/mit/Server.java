package ru.spbau.mit;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private @NotNull ExecutorService threadPool = Executors.newFixedThreadPool(4);


    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;

            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new ClientInteraction(clientSocket));
            }
        } catch (SocketException e) {
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create server socket");
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static class ClientInteraction implements Runnable {
        private Socket clientSocket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;

        ClientInteraction(@NotNull Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream())
            ) {
                this.inputStream = inputStream;
                this.outputStream = outputStream;
                boolean stopped = false;
                while (!stopped) {
                    int command = 0;
                    try {
                        command = inputStream.readInt();
                    } catch (Exception e) {
                        e.getSuppressed()[0].printStackTrace();
                    }
                    switch (command) {
                        case 1:
                            try {
                                executeList();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            break;
                        case 2:
                            try {
                                executeGet();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            break;
                        default:
                            stopped = true;
                            break;
                    }
                }
            } catch (SocketException e) {
            } catch (IOException e) {
                throw new IllegalStateException("Unable to create communication");
            }
        }

        public void executeList() throws IOException {
            final @NotNull String directoryPath = inputStream.readUTF();
            final @NotNull File directory = Paths.get(directoryPath).toFile();
            if (!directory.exists() || !directory.isDirectory()) {
                outputStream.writeInt(0);
            } else {
                final File[] files = directory.listFiles();
                if (files != null) {
                    outputStream.writeInt(files.length);
                    for (File file : files) {
                        outputStream.writeUTF(file.getName());
                        outputStream.writeBoolean(file.isDirectory());
                    }
                } else {
                    outputStream.writeInt(0);
                }
            }
        }

        public void executeGet() throws IOException {
            final @NotNull String filePath = inputStream.readUTF();
            final @NotNull File file = Paths.get(filePath).toFile();

            if (!file.exists()) {
                outputStream.writeInt(0);
            } else {
                final @NotNull FileInputStream fileInStream = new FileInputStream(file);
                int available = fileInStream.available();
                outputStream.writeLong(available);
                final @NotNull byte[] buffer = new byte[available];
                int bytesRead = fileInStream.read(buffer);
                if (bytesRead != available) {
                    throw new IOException("Only" + bytesRead + "bytes from " +
                            available + "of file content were read");
                }
                outputStream.write(buffer);
            }
        }
    }
}