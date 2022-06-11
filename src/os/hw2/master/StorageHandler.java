package os.hw2.master;

import os.hw2.Main;
import os.hw2.util.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class StorageHandler {
    private Socket storageSocket;
    private PrintStream storagePrintStream;
    private Scanner storageScanner;

    private int storagePort;

    public StorageHandler(int storagePort) {
        this.storagePort = storagePort;

        connectToStorage();
    }

    private void createStorageProcess() {
        try {
            Process process = new ProcessBuilder(
                    Main.commonArgs[0], Main.commonArgs[1], Main.commonArgs[2], Main.commonArgs[3], Main.commonArgs[4],
                    "os.hw2.storage.Storage",
                    String.valueOf(storagePort), String.valueOf(Main.numberOfWorkers)
            ).start();

            Logger.getInstance().log("Storage process created, PID: " + process.pid() + ", Port: " + storagePort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToStorage() {
        try {
            createStorageProcess();

            // Wait until process creation
            Thread.sleep(100);

            storageSocket = new Socket(InetAddress.getLocalHost(), storagePort);

            storagePrintStream = new PrintStream(storageSocket.getOutputStream());
            storageScanner = new Scanner(storageSocket.getInputStream());

            sendInitialMemory();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendInitialMemory() {
        storagePrintStream.println(Main.memoryString);
        storagePrintStream.flush();
    }
}
