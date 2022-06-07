package os.hw2;

import os.hw2.util.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Master {
    private int masterPort;

    private Process storageProcess;
    private Socket storageSocket;
    private PrintStream storagePrintStream;
    private Scanner storageScanner;

    private List<WorkerHandler> workerHandlers;

    public Master(){
        workerHandlers = new LinkedList<>();
    }

    private void createWorkers(){
        Logger.getInstance().log("Master is Creating workers...");

        for (int i = 0; i < Main.numberOfWorkers; i++){
            workerHandlers.add(new WorkerHandler());
        }
    }

    private void createStorageProcess() {
        try {
            Process process = new ProcessBuilder(
                    Main.commonArgs[0], Main.commonArgs[1], Main.commonArgs[2], Main.commonArgs[3],
                            Main.commonArgs[4], "os.hw2.Storage", String.valueOf(Main.storagePort)
            ).start();

            storageProcess = process;

            Logger.getInstance().log("Storage process created, PID: " + process.pid() + ", Port: " + Main.storagePort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToStorage() {
        try {
            createStorageProcess();

            // Wait until process creation
            Thread.sleep(100);

            storageSocket = new Socket(InetAddress.getLocalHost(), Main.storagePort);

            storagePrintStream = new PrintStream(storageSocket.getOutputStream());
            storageScanner = new Scanner(storageSocket.getInputStream());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initializeStorage(){
        // TODO
    }

    public void start(){
        connectToStorage();
        initializeStorage();

        // createWorkers();
    }
}
