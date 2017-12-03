package ru.spbau.mit.interaction.messages.server;

import org.jetbrains.annotations.NotNull;

public class ResponseGet extends ServerMessage {
    private byte[] fileContent;
    private boolean isFileValid = false;
    private long bytesSent = 0;
    private long fileSize = 0;

    public ResponseGet(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setPartOfFileContent(@NotNull byte[] fileContent, long bytesRead) {
        this.fileContent = fileContent;
        bytesSent += bytesRead;
    }

    public byte[] getPartOfFileContent() {
        return fileContent;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public void setInvalidFile() {
        isFileValid = false;
    }

    public boolean isFileValid() {
        return isFileValid;
    }
}
