package utils;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class IPv4 {
    private static final int BYTES_NUMBER = 4;
    private final byte[] ip;

    public IPv4(byte[] ip) {
        this.ip = ip;
    }

    public static IPv4 getIP(@NotNull DataInputStream in) throws IOException {
        byte[] ip = new byte[BYTES_NUMBER];
        int bytesRead = in.read(ip);
        if (bytesRead != BYTES_NUMBER) {
            throw new IllegalArgumentException("There is not IPv4 in the input stream");
        }
        return new IPv4(ip);
    }

    void write(@NotNull DataOutputStream out) throws IOException {
        out.write(ip);
    }

    @NotNull
    public InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getByAddress(ip);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof IPv4 && Arrays.equals(((IPv4) object).ip, ip);
    }
}