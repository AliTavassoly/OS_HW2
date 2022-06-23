package os.hw2.storage;

import os.hw2.Main;
import os.hw2.util.Logger;
import os.hw2.util.Message;
import os.hw2.util.MyGson;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class MasterHandler {
    private ServerSocket storageServerSocket;
    private PrintStream masterPrintStream;
    private Scanner masterScanner;

    private Storage storage;

    public MasterHandler(ServerSocket storageServerSocket, Storage storage) {
        this.storageServerSocket = storageServerSocket;
        this.storage = storage;

        connectToMaster();
    }

    public void connectToMaster() {
        try {
            Socket socket = storageServerSocket.accept();
            masterPrintStream = new PrintStream(socket.getOutputStream());
            masterScanner = new Scanner(socket.getInputStream());

            Logger.getInstance().log("Master connected to Storage");
        } catch (
        IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        listenToMaster();
    }

    public int initializeMemory(ArrayList<Integer> memory) {
        String memoryString = masterScanner.nextLine();

        String[] listOfData = memoryString.split(" ");

        for (int i = 0; i < listOfData.length; i++)
            memory.add(Integer.parseInt(listOfData[i]));

        Logger.getInstance().log("Memory initialized with: " + memory);

        return memory.size();
    }


    public Main.Deadlock initializeDeadlock() {
        Main.Deadlock deadlock = MyGson.getGson().fromJson(masterScanner.nextLine(), Main.Deadlock.class);
        return deadlock;
    }

    private void listenToMaster() {
        new Thread(() -> {
            while (true) {
                Message message = MyGson.getGson().fromJson(masterScanner.nextLine(), Message.class);

                Logger.getInstance().log("New message from master: " + message);

                newMessageFromWorker(message);
            }
        }).start();
    }

    private void newMessageFromWorker(Message message) {
        switch (message.getType()) {
            case UNLOCK:
                storage.unlockTask(message.getTask());
                break;
            case REMOVE_WAITER:
                storage.removeWaiters(message.getTask());
                break;
        }
    }
}
