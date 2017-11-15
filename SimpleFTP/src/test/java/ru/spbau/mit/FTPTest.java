package ru.spbau.mit;

import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.*;

public class FTPTest {

    static final int SERVER_RUNNING_TIME = 50;
    static final Server server = new Server();
    static int PORT = 4444;
    static final Thread serverThread = new Thread(() -> server.start(PORT));

    static {
        serverThread.start();
        try {
            Thread.sleep(SERVER_RUNNING_TIME);
        } catch (InterruptedException e) {
        }
    }

    @AfterClass
    public static void stopServer() throws Exception {
        server.stop();
    }

    @Test
    public void simpleGetTest() throws Exception {
        Writer writer = new StringWriter();
        Client client = new Client();
        client.connect("localhost", PORT);
        client.executeGet(System.getProperty("user.dir") +
                        "/src/test/resources/test",
                writer);
        client.disconnect();
        assertEquals("hello, client", writer.toString().trim());
    }

    @Test
    public void simpleListTest() throws Exception {
        String pathToDirectory = System.getProperty("user.dir")
                + "/src/test/resources/for_client";
        Writer writer = new StringWriter();
        Client client = new Client();
        client.connect("localhost", PORT);
        client.executeList(System.getProperty("user.dir")
                + "/src/test/resources/for_client", writer);
        String[] files = writer.toString().split("\n");
        File directory = new File(pathToDirectory);
        assertTrue(directory.isDirectory());
        File[] actualFiles = directory.listFiles();
        assertNotNull(actualFiles);
        for (int i = 0; i < files.length; i++) {
            assertEquals(actualFiles[i].getName(), files[i].split(":", 2)[0].trim());
        }
    }
}
