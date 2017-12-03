package ru.spbau.mit.interaction.messages.server;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResponseList extends ServerMessage {
    @NotNull
    private List<FileInfo> filesPaths = new ArrayList<>();
    private boolean isDirectoryValid = false;

    public void setInvalidDirectory() {
        isDirectoryValid = false;
    }

    public boolean isDirectoryValid() {
        return isDirectoryValid;
    }

    @NotNull
    public List<FileInfo> getFilesPaths() {
        return filesPaths;
    }

    public void setFilesPaths(@NotNull List<FileInfo> filesPaths) {
        this.filesPaths = filesPaths;
        isDirectoryValid = true;
    }

    public int getFilesNumber() {
        if (isDirectoryValid) {
            return filesPaths.size();
        } else {
            return 0;
        }
    }

    public static class FileInfo implements Serializable {
        private final String filePath;
        private final boolean isDirectory;

        public FileInfo(String filePath, boolean isDirectory) {
            this.filePath = filePath;
            this.isDirectory = isDirectory;
        }

        public String getFilePath() {
            return filePath;
        }

        public boolean getIsDirectory() {
            return isDirectory;
        }
    }
}
