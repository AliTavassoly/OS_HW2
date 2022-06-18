package os.hw2.master;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import os.hw2.Main;
import os.hw2.util.Message;
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

    private GsonBuilder gsonBuilder;
    private Gson gson;

    private Process process;

    public StorageHandler(int storagePort) {
        this.storagePort = storagePort;

        connectToStorage();

        createGson();
    }

    private void createGson(){
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    private void createStorageProcess() {
        try {
            process = new ProcessBuilder(
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

    public void sendMessageToWorker(Message message) {
        storagePrintStream.println(gson.toJson(message));
        storagePrintStream.flush();
    }

    public void shutDown() {
        process.destroy();
        try {
            storageSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
