package ru.spbau.mit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.spbau.mit.client.Client;
import ru.spbau.mit.client.FTPClient;
import ru.spbau.mit.exceptions.InternalServerException;
import ru.spbau.mit.server.FTPServer;
import ru.spbau.mit.server.Server;

import java.io.*;

import static org.junit.Assert.*;

public class FTPTest {

    private static final Server server = new FTPServer();
    private static final int PORT = 4444;
    private static final int FILE_BUFFER_SIZE = 4096;
    private static final int THREADS_NUMBER = 4;

    @BeforeClass
    public static void startServer() throws Exception {
        final int SERVER_RUNNING_TIME = 50;
        final Thread serverThread = new Thread(() -> {
            try {
                server.start(PORT, THREADS_NUMBER, FILE_BUFFER_SIZE);
            } catch (InternalServerException e) {
                e.printStackTrace();
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
        server.stop();
    }

    @Test
    public void simpleGetTest() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        Client client = new FTPClient();
        client.connect("localhost", PORT);
        client.executeGet(System.getProperty("user.dir") +
                        "/src/test/resources/test",
                dataOutputStream);
        client.disconnect();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        DataInputStream dataInputStream = new DataInputStream(in);
        assertEquals(13, dataInputStream.readLong());
        byte[] buffer = new byte[13];
        assertEquals(13, dataInputStream.read(buffer));
        assertEquals("hello, client", new String(buffer));
    }

    @Test
    public void binaryFileGetTest() throws Exception {
        String filePath = System.getProperty("user.dir") + "/src/test/resources/test_bin";
        File file = new File(filePath);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        Client client = new FTPClient();
        client.connect("localhost", PORT);
        client.executeGet(filePath, dataOutputStream);
        client.disconnect();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        DataInputStream dataInputStream = new DataInputStream(in);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBuffer = new byte[(int) file.length()];
        fileInputStream.read(fileBuffer);
        assertEquals(file.length(), dataInputStream.readLong());
        byte[] fileBufferFromServer = new byte[FILE_BUFFER_SIZE];
        for (int i = 0; i < file.length(); ++i) {
            if (i % FILE_BUFFER_SIZE == 0) {
                dataInputStream.read(fileBufferFromServer);
            }
            assertEquals(fileBuffer[i], fileBufferFromServer[i % FILE_BUFFER_SIZE]);
        }
    }

    @Test
    public void simpleListTest() throws Exception {
        String pathToDirectory = System.getProperty("user.dir")
                + "/src/test/resources/for_client";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        Client client = new FTPClient();
        client.connect("localhost", PORT);
        client.executeList(pathToDirectory, dataOutputStream);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        DataInputStream dataInputStream = new DataInputStream(in);

        File directory = new File(pathToDirectory);
        assertTrue(directory.isDirectory());
        File[] actualFiles = directory.listFiles();
        assertNotNull(actualFiles);
        assertEquals(actualFiles.length, dataInputStream.readInt());
        for (File actualFile : actualFiles) {
            assertEquals(actualFile.getAbsolutePath(), dataInputStream.readUTF());
            assertEquals(actualFile.isDirectory(), dataInputStream.readBoolean());
        }
    }

    @Test
    public void listRootDirectoryTest() throws Exception {
        String pathToDirectory = "/";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        Client client = new FTPClient();
        client.connect("localhost", PORT);
        client.executeList(pathToDirectory, dataOutputStream);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        DataInputStream dataInputStream = new DataInputStream(in);

        File directory = new File(pathToDirectory);
        assertTrue(directory.isDirectory());
        File[] actualFiles = directory.listFiles();
        assertNotNull(actualFiles);
        assertEquals(actualFiles.length, dataInputStream.readInt());
        for (File actualFile : actualFiles) {
            assertEquals(actualFile.getAbsolutePath(), dataInputStream.readUTF());
            assertEquals(actualFile.isDirectory(), dataInputStream.readBoolean());
        }
    }
}
