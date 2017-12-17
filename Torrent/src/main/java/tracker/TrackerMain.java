package tracker;

import java.util.Scanner;

public class TrackerMain {
    public static void main(String[] args) {
        Tracker tracker = new TrackerImpl();
        Scanner scanner = new Scanner(System.in);
//        String command = scanner.next();
        String command = "start";
        boolean stopped = false;
        while (!stopped) {
            switch (command) {
                case "start":
                    System.out.println("Enter a number of threads");
                    //Thread trackerThread = new Thread(() -> tracker.start(scanner.nextInt()));
                    Thread trackerThread = new Thread(() -> tracker.start(4));
                    trackerThread.start();
                    System.out.println("The tracker is running");
                    break;
                case "stop":
                    stopped = true;
                    tracker.stop();
                    System.out.println("The tracker is stopped");
                default:
                    printUsage();
            }
            command = scanner.next();
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("start: ");
        System.out.println("starts the tracker");
        System.out.println("stop: ");
        System.out.println("stops the tracker");
    }
}
