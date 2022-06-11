package os.hw2.storage;

import os.hw2.Main;
import os.hw2.util.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    private int storagePort, numberOfWorkers;

    private ServerSocket serverSocket;
    private PrintStream masterPrintStream;
    private Scanner masterScanner;

    private PrintStream[] workerPrintStreams;
    private Scanner[] workerScanners;

    private ArrayList memory;

    public Storage(int storagePort, int numberOfWorkers) {
        this.storagePort = storagePort;
        this.numberOfWorkers = numberOfWorkers;
        this.workerScanners = new Scanner[numberOfWorkers];
        this.workerPrintStreams = new PrintStream[numberOfWorkers];

        memory = new ArrayList();
    }

    public void start(){
        try {
            serverSocket = new ServerSocket(storagePort);

            Socket socket = serverSocket.accept();
            masterPrintStream = new PrintStream(socket.getOutputStream());
            masterScanner = new Scanner(socket.getInputStream());

            Logger.getInstance().log("Master connected to Storage");

            waitForWorkersToConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForWorkersToConnect() {
        Thread waitForWorkersConnectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < numberOfWorkers; i++){
                    try {
                        Socket socket = serverSocket.accept();
                        PrintStream printStream = new PrintStream(socket.getOutputStream());
                        Scanner scanner = new Scanner(socket.getInputStream());

                        int workerID = Integer.parseInt(scanner.nextLine());

                        Logger.getInstance().log("Worker with ID: " + workerID + " connected");

                        workerPrintStreams[workerID] = printStream;
                        workerScanners[workerID] = scanner;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        waitForWorkersConnectThread.start();
    }

    public static void logCreation(){
        long pid = ProcessHandle.current().pid();
        Logger.getInstance().log("Process start, PID: " + pid);
    }

    public static void main(String[] args) {
        Logger.processName = "Storage";
        logCreation();

        int port = Integer.parseInt(args[0]);
        int numberOfWorkers = Integer.parseInt(args[1]);

        Storage storage = new Storage(port, numberOfWorkers);
        storage.start();
    }
}
