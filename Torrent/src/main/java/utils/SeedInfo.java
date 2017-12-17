package utils;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;

public class SeedInfo {
    private final IPv4 ip;
    private final short port;

    public SeedInfo(IPv4 ip, short port) {
        this.ip = ip;
        this.port = port;
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

    public void write(@NotNull DataOutputStream out) throws IOException {
        ip.write(out);
        out.writeShort(port);
    }
}
