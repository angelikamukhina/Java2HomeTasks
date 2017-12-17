package tracker;

import utils.FileInfo;
import utils.SeedInfo;
import utils.TorrentConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TrackerState {
    private Map<Integer, FileInfo> files = new HashMap<>();
    private Map<Integer, List<SeedInfo>> seeds = new HashMap<>();

    synchronized public int addNewFile(FileInfo fileInfo) {
        int fileId = files.size();
        files.put(fileId, fileInfo);
        seeds.put(fileId, new ArrayList<>());
        return fileId;
    }

    synchronized boolean addNewSeedIfAbsent(int fileId, SeedInfo seedInfo) {
        if (!seeds.get(fileId).contains(seedInfo)) {
            seeds.get(fileId).add(seedInfo);
        }
        return true;
    }

    public synchronized void storeToFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            Files.createFile(Paths.get(filePath));
        }
        FileOutputStream fout = new FileOutputStream(file);
        DataOutputStream out = new DataOutputStream(fout);

        out.writeInt(files.size());
        for (int fileId : files.keySet()) {
            out.writeInt(fileId);
            FileInfo fileInfo = files.get(fileId);
            fileInfo.write(out);
        }
    }

    public synchronized void getFromFile(String filePath) throws IOException {
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

    synchronized Map<Integer, FileInfo> getAvailableFiles() {
        return files;
    }
}
