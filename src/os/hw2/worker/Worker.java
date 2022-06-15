package os.hw2.worker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import os.hw2.Message;
import os.hw2.Task;
import os.hw2.util.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Worker {
    private int workerPort, storagePort;

    private Socket storageSocket;
    private PrintStream storagePrintStream;
    private Scanner storageScanner;

    private ServerSocket serverSocket;
    private PrintStream masterPrintStream;
    private Scanner masterScanner;

    private GsonBuilder gsonBuilder;
    private Gson gson;

    private int id;

    private Integer valueLookingFor = null;

    public Worker(int workerPort, int storagePort, int id) {
        this.workerPort = workerPort;
        this.storagePort = storagePort;
        this.id = id;

        createGson();

        logCreation();
    }

    private void createGson() {
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    public void start(){
        try {
            serverSocket = new ServerSocket(workerPort);

            Socket socket = serverSocket.accept();
            masterPrintStream = new PrintStream(socket.getOutputStream());
            masterScanner = new Scanner(socket.getInputStream());

            Logger.getInstance().log("Master connected to Worker");

            connectToStorage();

            listenToMaster();

            listenToStorage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenToMaster() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Message message = gson.fromJson(masterScanner.nextLine(), Message.class);
                    newMessageFromMaster(message);
                }
            }
        });
        thread.start();
    }

    private void listenToStorage() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Message message = gson.fromJson(storageScanner.nextLine(), Message.class);
                    newMessageFromStorage(message);
                }
            }
        });
        thread.start();
    }

    private void newMessageFromMaster(Message message) {
        // TODO
        Logger.getInstance().log("New message from master: " + message);

        switch (message.getType()) {
            case REQUEST:
                break;
            case ASSIGN:
                break;
            case INTERRUPT:
                break;
            case TASKBACK:
                break;
            case RESULT:
                break;
        }
    }

    private void newMessageFromStorage(Message message) {
        // TODO
        Logger.getInstance().log("New message from storage: " + message.getType());
    }

    private void sendIDToStorage(){
        storagePrintStream.println(id);
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

    public void logCreation(){
        long pid = ProcessHandle.current().pid();
        Logger.getInstance().log("Process start, PID: " + pid + ", worker ID: " + id);
    }

    private void runTask(Task task) {

    }

    public static void main(String[] args) {
        Logger.processName = "Worker";

        int workerPort = Integer.parseInt(args[0]);
        int storagePort = Integer.parseInt(args[1]);
        int id = Integer.parseInt(args[2]);

        Worker worker = new Worker(workerPort, storagePort, id);

        worker.start();
    }
}
