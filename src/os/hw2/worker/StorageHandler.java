package os.hw2.worker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import os.hw2.Message;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class StorageHandler {
    private Socket storageSocket;
    private PrintStream storagePrintStream;
    private Scanner storageScanner;

    private int workerID, storagePort;

    private Worker worker;

    private GsonBuilder gsonBuilder;
    private Gson gson;

    public StorageHandler(int workerID, int storagePort, Worker worker) {
        this.workerID = workerID;
        this.storagePort = storagePort;
        this.worker = worker;

        createGson();

        connectToStorage();
    }

    private void createGson() {
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    private void sendIDToStorage(){
        storagePrintStream.println(workerID);
        storagePrintStream.flush();
    }

    private void connectToStorage() {
        try {
            storageSocket = new Socket(InetAddress.getLocalHost(), storagePort);
            storagePrintStream = new PrintStream(storageSocket.getOutputStream());
            storageScanner = new Scanner(storageSocket.getInputStream());

            sendIDToStorage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListening() {
        Thread thread = new Thread(() -> {
            while (true) {
                Message message = gson.fromJson(storageScanner.nextLine(), Message.class);
                worker.newMessageFromStorage(message);
            }
        });
        thread.start();
    }


}
