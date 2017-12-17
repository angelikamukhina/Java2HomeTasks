package client;

import client.peer.Peer;
import client.peer.PeerImpl;
import client.tracker_client.TrackerClient;
import client.tracker_client.TrackerClientImpl;
import exceptions.InternalTrackerException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import tracker.Tracker;
import tracker.TrackerImpl;
import utils.FileInfo;
import utils.IPv4;
import utils.SeedInfo;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TorrentTest {
    private static final Tracker tracker = new TrackerImpl();
    private static final int THREADS_NUMBER = 4;

    @BeforeClass
    public static void startTracker() throws Exception {
        final int SERVER_RUNNING_TIME = 50;
        final Thread serverThread = new Thread(() -> {
            try {
                tracker.start(THREADS_NUMBER);
            } catch (InternalTrackerException exception) {
                exception.printStackTrace();
            }
        });
        serverThread.start();
        try {
            Thread.sleep(SERVER_RUNNING_TIME);
        } catch (InterruptedException ignored) {
        }
    }

    @AfterClass
    public static void stopServer() throws Exception {
        tracker.stop();
        File clientState = new File(System.getProperty("user.dir") + "/src/main/resources/clientState");
        clientState.delete();
        File trackerState = new File(System.getProperty("user.dir") + "/src/main/resources/trackerState");
        trackerState.delete();

    }

    @Test
    public void simpleListTest() {
        List<TrackerClient> clients = new ArrayList<>();
        File testDirectory = new File(System.getProperty("user.dir") + "/src/test/resources");
        File[] files = testDirectory.listFiles();
        assert files != null;
        for (int client = 0; client < files.length; ++client) {
            TrackerClient trackerClient = new TrackerClientImpl();
            trackerClient.start("localhost");
            clients.add(trackerClient);
        }

        Map<Integer, File> filesIds = new HashMap<>();
        for (int fileNo = 0; fileNo < files.length; ++fileNo) {
            filesIds.put(clients.get(fileNo).uploadNewFile(files[fileNo].getAbsolutePath()), files[fileNo]);
        }
        Map<Integer, FileInfo> filesList = clients.get(0).getFilesList();
        for (int fileId : filesIds.keySet()) {
            assertEquals(filesIds.get(fileId).getName(), filesList.get(fileId).getName());
            assertEquals(filesIds.get(fileId).length(), filesList.get(fileId).getSize());
        }

        for (TrackerClient client : clients) {
            client.stop();
        }
    }

    @Test
    public void simpleUploadTest() throws Exception {
        Client client = new ClientImpl();
        client.start("localhost", (short) 4444, 4);
        File testFile = new File(System.getProperty("user.dir") + "/src/test/resources/test1");
        int fileId = client.uploadFile(testFile.getAbsolutePath());
        assertEquals(testFile.getName(), client.getAvailableFiles().get(fileId).getName());
        assertEquals(testFile.length(), client.getAvailableFiles().get(fileId).getSize());
    }

    @Test
    public void simpleSourcesTest() throws Exception {
        Client client = new ClientImpl();
        short seedPort = 10000;
        client.start("localhost", seedPort, 4);
        int fileId = client.uploadFile(System.getProperty("user.dir") + "/src/test/resources/test1");
        TrackerClient trackerClient = new TrackerClientImpl();
        trackerClient.start("localhost");
        List<SeedInfo> seeds = trackerClient.getSeeds(fileId);
        assertTrue(seeds.stream().map(SeedInfo::getPort).collect(Collectors.toList()).contains(seedPort));
        client.stop();
        trackerClient.stop();
    }

    @Test
    public void simpleStatTest() throws Exception {
        Client client = new ClientImpl();
        short seedPort = 11000;
        client.start("localhost", seedPort, 4);
        int fileId = client.uploadFile(System.getProperty("user.dir") + "/src/test/resources/test1");
        Peer peer = new PeerImpl();
        peer.start(new SeedInfo(new IPv4(InetAddress.getLocalHost().getAddress()), seedPort));
        BitSet parts = peer.getAvailablePartsInfo(fileId);
        assertEquals(parts.length(), parts.cardinality());
    }

    @Test
    public void simpleGetTest() throws Exception {
        Client client = new ClientImpl();
        short seedPort = 12000;
        client.start("localhost", seedPort, 4);
        int fileId = client.uploadFile(System.getProperty("user.dir") + "/src/test/resources/test1");
        Peer peer = new PeerImpl();
        peer.start(new SeedInfo(new IPv4(InetAddress.getLocalHost().getAddress()), seedPort));
        byte[] content = peer.getPart(fileId, 0);
        File file = new File(System.getProperty("user.dir") + "/src/test/resources/test1");
        FileInputStream fin = new FileInputStream(file);
        byte[] fileContent = new byte[(int) file.length()];
        int bytesRead = fin.read(fileContent);
        byte[] array = Arrays.copyOfRange(content, 0, bytesRead);
        for (int byteNo = 0; byteNo < bytesRead; byteNo++) {
            assertEquals(fileContent[byteNo], array[byteNo]);
        }
    }
}