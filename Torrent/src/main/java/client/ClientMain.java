package client;

import utils.FileInfo;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter tracker host: ");
//        String host = scanner.nextLine();
        String host = "localhost";
        System.out.println("Enter client seed port: ");
        short seedPort = scanner.nextShort();
        System.out.println("Enter number of threads for seed: ");
//        int threadsNumber = scanner.nextInt();
        int threadsNumber = 4;
        Client client = new ClientImpl();
        client.start(host, seedPort, threadsNumber);
        String command = scanner.next();
        while (!command.equals("stop")) {
            switch (command) {
                case "available": {
                    Map<Integer, FileInfo> availableFiles = client.getAvailableFiles();
                    printAvailableFiles(availableFiles);
                    break;
                }
                case "upload": {
                    String filePath = scanner.next();
                    int fileId = 0;
                    try {
                        fileId = client.uploadFile(filePath);
                    } catch (IOException e) {
                        System.out.println("Wrong file path, try again");
                    }
                    System.out.println("file id: " + fileId);
                    break;
                }
                case "download": {
                    int fileId = scanner.nextInt();
                    String filePath = scanner.next();
                    try {
                        client.downloadFile(fileId, filePath);
                    } catch (IOException e) {
                        System.out.println("Wrong file path, try again");
                    }
                    break;
                }
                default: {
                    System.out.println("Wrong command");
                    printUsage();
                }
            }
            command = scanner.next();
        }
        client.stop();
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("available: ");
        System.out.println("gets list of available for downloading files (file id and name)");
        System.out.println("upload: ");
        System.out.println("upload (file path): ");
        System.out.println("adds the file to tracker list");
        System.out.println("download (file id) (file path to download to): ");
        System.out.println("downloads file");
        System.out.println("stop: ");
        System.out.println("stops the client work");
    }

    private static void printAvailableFiles(Map<Integer, FileInfo> files) {
        files.forEach((key, value) -> System.out.println(key + " " + value.getName()));
    }
}
