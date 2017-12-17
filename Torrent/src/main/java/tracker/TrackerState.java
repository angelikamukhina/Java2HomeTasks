package tracker;

import org.jetbrains.annotations.NotNull;
import utils.FileInfo;
import utils.SeedInfo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrackerState {
    @NotNull
    private final Map<Integer, FileInfo> files = new HashMap<>();
    @NotNull
    private final Map<Integer, List<SeedInfo>> seeds = new HashMap<>();

    synchronized public int addNewFile(FileInfo fileInfo) {
        int fileId = files.size();
        files.put(fileId, fileInfo);
        seeds.put(fileId, new ArrayList<>());
        return fileId;
    }

    synchronized void addNewSeedIfAbsent(int fileId, SeedInfo seedInfo) {
        if (!seeds.get(fileId).contains(seedInfo)) {
            seeds.get(fileId).add(seedInfo);
        }
    }

    public synchronized void storeToFile(@NotNull String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            Files.createFile(Paths.get(filePath));
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        DataOutputStream out = new DataOutputStream(fileOutputStream);

        out.writeInt(files.size());
        for (int fileId : files.keySet()) {
            out.writeInt(fileId);
            FileInfo fileInfo = files.get(fileId);
            fileInfo.write(out);
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
            FileInfo fileInfo = FileInfo.getFileInfo(in);
            files.put(fileId, fileInfo);
        }
    }

    synchronized List<SeedInfo> getSeeds(int fileId) {
        return seeds.get(fileId);
    }

    @NotNull
    synchronized Map<Integer, FileInfo> getAvailableFiles() {
        return files;
    }
}
