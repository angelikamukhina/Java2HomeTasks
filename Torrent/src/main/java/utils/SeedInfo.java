package utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SeedInfo {
    private final IPv4 ip;
    private final short port;
    public SeedInfo(IPv4 ip, short port) {
        this.ip = ip;
        this.port = port;
    }
    public static SeedInfo getSeedInfo(Socket socket) {
        if (!socket.isClosed()) {
            short port = (short) socket.getPort();
            byte[] ip = socket.getRemoteSocketAddress().toString().getBytes();
            return new SeedInfo(new IPv4(ip), port);
        } else {
            throw new IllegalArgumentException("Socket is closed");
        }
    }

    public IPv4 getIp() {
        return ip;
    }

    public short getPort() {
        return port;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof SeedInfo &&
                ((SeedInfo) object).ip.equals(ip) &&
                ((SeedInfo) object).port == port;
    }

    public void write(DataOutputStream out) throws IOException {
        ip.write(out);
        out.writeShort(port);
    }
}
