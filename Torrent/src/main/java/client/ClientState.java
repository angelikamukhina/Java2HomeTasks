package client;

import exceptions.NotWholeFileException;
import org.jetbrains.annotations.NotNull;
import utils.FileInfo;
import utils.TorrentConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ClientState {
    @NotNull
    private final Map<Integer, BitSet> fileParts = new HashMap<>();
    @NotNull
    private final Map<Integer, List<byte[]>> filePartsContent = new HashMap<>();
    private Map<Integer, FileInfo> filesInfo;

    public synchronized BitSet getParts(int fileId) {
        if (fileParts.containsKey(fileId)) {
            return fileParts.get(fileId);
        } else {
            long fileSize = filesInfo.get(fileId).getSize();
            return new BitSet((int) (fileSize / TorrentConstants.PART_SIZE + 1));
        }
    }

    public synchronized void addWholeFile(int fileId, @NotNull String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream fin = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fin);
            int partsNumber = (int) (file.length() / TorrentConstants.PART_SIZE + 1);
            BitSet parts = new BitSet(partsNumber);
            List<byte[]> partsContent = new ArrayList<>();
            for (int partNo = 0; partNo < partsNumber; ++partNo) {
                byte[] filePart = new byte[TorrentConstants.PART_SIZE];
                in.read(filePart);
                parts.set(partNo);
                partsContent.add(filePart);
            }
            fileParts.put(fileId, parts);
            filePartsContent.put(fileId, partsContent);
        }
    }

    public void storePartsToFile(int fileId, @NotNull String filePath)
            throws NotWholeFileException, IOException {
        if (fileParts.get(fileId).cardinality() != fileParts.get(fileId).length()) {
            throw new NotWholeFileException("File " + filesInfo.get(fileId).getName()
                    + " is not downloaded yet");
        }
        File file = new File(filePath);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        for (byte[] content : filePartsContent.get(fileId)) {
            fileOutputStream.write(content);
        }
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public synchronized void addPartContent(int fileId, int part, byte[] content) {
        if (!filePartsContent.containsKey(fileId)) {
            long fileSize = filesInfo.get(fileId).getSize();
            List<byte[]> partsContents = new ArrayList<>();
            for (int partNo = 0; partNo < (int) (fileSize / TorrentConstants.PART_SIZE) + 1; ++partNo) {
                byte[] initial = new byte[0];
                partsContents.add(initial);
            }
            partsContents.set(part, content);
            filePartsContent.put(fileId, partsContents);
            fileParts.put(fileId, new BitSet((int) (fileSize / TorrentConstants.PART_SIZE)));
        } else {
            filePartsContent.get(fileId).set(part, content);
            fileParts.get(fileId).set(part);
        }
    }

    @NotNull
    public Set<Integer> getAvailableFilesIds() {
        return fileParts.keySet();
    }

    public synchronized byte[] getPartContent(int fileId, int part) {
        return filePartsContent.get(fileId).get(part);
    }

    public synchronized void updateAvailableForDownloadingFiles(Map<Integer, FileInfo> files) {
        filesInfo = files;
    }

    public synchronized void storeToFile(@NotNull String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            Files.createFile(Paths.get(filePath));
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        DataOutputStream out = new DataOutputStream(fileOutputStream);

        out.writeInt(fileParts.size());
        for (int fileId : fileParts.keySet()) {
            out.writeInt(fileId);
            BitSet parts = fileParts.get(fileId);
            out.writeInt(parts.cardinality());
            for (int part = 0; part < parts.length(); ++part) {
                if (parts.get(part)) {
                    out.writeInt(part);
                    out.write(filePartsContent.get(fileId).get(part));
                }
            }
        }
    }

    public synchronized void getFromFile(@NotNull String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            Files.createFile(Paths.get(filePath));
            return;
        }
        if (file.length() == 0) {
            return;
        }
        FileInputStream fin = new FileInputStream(file);
        DataInputStream in = new DataInputStream(fin);
        int filesNumber = in.readInt();
        for (int fileCounter = 0; fileCounter < filesNumber; ++fileCounter) {
            int fileId = in.readInt();
            int partsNumber = in.readInt();
            BitSet parts = new BitSet(partsNumber);
            List<byte[]> partsContents = new ArrayList<>();
            for (int partNo = 0; partNo < partsNumber; ++partNo) {
                byte[] content = new byte[0];
                partsContents.add(content);
            }
            for (int part = 0; part < partsNumber; ++part) {
                int partNo = in.readInt();
                parts.set(partNo);
                byte[] content = new byte[TorrentConstants.PART_SIZE];
                in.read(content);
                partsContents.set(partNo, content);
            }
            fileParts.put(fileId, parts);
            filePartsContent.put(fileId, partsContents);
        }
    }
}
