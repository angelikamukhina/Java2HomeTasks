package utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FileInfo {
    private final String name;
    private final long size;

    public FileInfo(String name, long size) {
        this.name = name;
        this.size = size;
    }

    public static FileInfo getFileInfo(DataInputStream in) throws IOException {
        String name = in.readUTF();
        long size = in.readLong();
        return new FileInfo(name, size);
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeLong(size);
    }
}
