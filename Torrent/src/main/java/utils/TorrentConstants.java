package utils;

public class TorrentConstants {
    public static final int PART_SIZE = 1_048_576 /* 1Mb */;
    public static final short TRACKER_PORT = 8081;
    public static final String clientStateFile = System.getProperty("user.dir")
            + "/src/main/resources/clientState";
    public static final String trackerStateFile = System.getProperty("user.dir")
            + "/src/main/resources/trackerState";
}
