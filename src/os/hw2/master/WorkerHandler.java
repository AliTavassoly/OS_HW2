package os.hw2.master;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import os.hw2.Main;
import os.hw2.util.Message;
import os.hw2.Task;
import os.hw2.util.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class WorkerHandler {
    private Socket workerSocket;
    private PrintStream workerPrintStream;
    private Scanner workerScanner;

    private int workerPort, id;

    private GsonBuilder gsonBuilder;
    private Gson gson;

    private boolean isBusy = false;

    private Process process;

    private Master master;

    public WorkerHandler(int workerPort, Master master){
        this.workerPort = workerPort;
        this.id = workerPort - Main.firstWorkerPort;
        this.master = master;

        createGson();

        connectToWorker();

        startListeningToWorker();
    }

    private void createGson(){
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    private void createWorkerProcess() {
        try {
            process = new ProcessBuilder(
                    Main.commonArgs[0], Main.commonArgs[1], Main.commonArgs[2], Main.commonArgs[3], Main.commonArgs[4],
                    "os.hw2.worker.Worker",
                    String.valueOf(workerPort), String.valueOf(Main.storagePort), String.valueOf(workerPort - Main.firstWorkerPort)
            ).start();

            Logger.getInstance().log("Worker process created, PID: " + process.pid() + ", Port: " + workerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startListeningToWorker() {
        Thread thread = new Thread(() -> {
            while (true) {
                Message message = gson.fromJson(workerScanner.nextLine(), Message.class);
                Logger.getInstance().log("New message from worker: " + message);

                newMessage(message);
            }
        });
        thread.start();
    }

    private void connectToWorker() {
        try {
            createWorkerProcess();

            // Wait until process creation
            Thread.sleep(100);

            workerSocket = new Socket(InetAddress.getLocalHost(), workerPort);

            workerPrintStream = new PrintStream(workerSocket.getOutputStream());
            workerScanner = new Scanner(workerSocket.getInputStream());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void runTask(Task task) {
        isBusy = true;

        Message message = new Message(Message.Type.ASSIGN, Message.Sender.MASTER, task);
        sendMessage(message);
    }

    public boolean isBusy(){
        return isBusy;
    }

    public int getId(){
        return id;
    }

    public void sendMessage(Message message) {
        workerPrintStream.println(gson.toJson(message));
        workerPrintStream.flush();
    }

    public void shutDown() {
        process.destroy();
        try {
            workerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void taskResult(Task task) {
        master.taskResult(task);
        isBusy = false;
    }

    private void newMessage(Message message) {
        switch (message.getType()) {
            case RESULT:
                taskResult(message.getTask());
                break;
        }
    }
}
