package os.hw2.worker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import os.hw2.util.Message;
import os.hw2.util.Logger;
import os.hw2.util.MyGson;

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

    public StorageHandler(int workerID, int storagePort, Worker worker) {
        this.workerID = workerID;
        this.storagePort = storagePort;
        this.worker = worker;

        connectToStorage();
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
        new Thread(() -> {
            while (true) {
                Message message = MyGson.getGson().fromJson(storageScanner.nextLine(), Message.class);

                Logger.getInstance().log("New message from storage: " + message);

                newMessageFromStorage(message);
            }
        }).start();
    }

    public void getCellValue(int cellNumber) {
        Message message = new Message(Message.Type.CELLREQUEST, Message.Sender.WORKER, cellNumber, workerID);
        sendMessageToStorage(message);
    }

    private void sendMessageToStorage(Message message) {
        storagePrintStream.println(MyGson.getGson().toJson(message, Message.class));
        storagePrintStream.flush();
    }

    public void newMessageFromStorage(Message message) {
        switch (message.getType()) {
            case CELLRESPONSE:
                worker.cellResponse(message);
                break;
        }
    }

    public void shutDown() {
        try {
            storageSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
