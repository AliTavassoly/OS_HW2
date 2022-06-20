package os.hw2.worker;

import os.hw2.util.Message;
import os.hw2.util.Logger;
import os.hw2.util.MyGson;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MasterHandler {
    private ServerSocket masterServerSocket;
    private PrintStream masterPrintStream;
    private Scanner masterScanner;

    private int workerPort;

    private Worker worker;

    public MasterHandler(int workerPort, Worker worker) {
        this.workerPort = workerPort;
        this.worker = worker;

        try {
            masterServerSocket = new ServerSocket(workerPort);

            Socket socket = masterServerSocket.accept();
            masterPrintStream = new PrintStream(socket.getOutputStream());
            masterScanner = new Scanner(socket.getInputStream());

            Logger.getInstance().log("Master connected to Worker");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListening() {
        new Thread(() -> {
            while (true) {
                Message message = MyGson.getGson().fromJson(masterScanner.nextLine(), Message.class);

                Logger.getInstance().log("New message from master: " + message);

                newMessageFromMaster(message);
            }
        }).start();
    }

    public void sendMessageToMaster(Message message) {
        masterPrintStream.println(MyGson.getGson().toJson(message, Message.class));
        masterPrintStream.flush();
    }

    public void shutDown() {
        try {
            masterServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newMessageFromMaster(Message message) {
        switch (message.getType()) {
            case ASSIGN:
                worker.runTask(message.getTask());
                break;
            case INTERRUPT:
                worker.interruptTask(message.getTaskID());
                break;
            case TASKBACK:
                break;
            case RESULT:
                break;
        }
    }
}
