package os.hw2.master;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import os.hw2.Main;
import os.hw2.Task;
import os.hw2.util.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class WorkerHandler {
    private Process workerProcess;
    private Socket workerSocket;
    private PrintStream workerPrintStream;
    private Scanner workerScanner;

    private int workerPort, id;

    private GsonBuilder gsonBuilder;
    private Gson gson;

    private boolean isBusy = false;

    public WorkerHandler(int workerPort){
        this.workerPort = workerPort;
        this.id = workerPort - Main.firstWorkerPort;

        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        connectToWorker();
    }

    private void createWorkerProcess() {
        try {
            Process process = new ProcessBuilder(
                    Main.commonArgs[0], Main.commonArgs[1], Main.commonArgs[2], Main.commonArgs[3], Main.commonArgs[4],
                    "os.hw2.worker.Worker",
                    String.valueOf(workerPort), String.valueOf(Main.storagePort), String.valueOf(workerPort - Main.firstWorkerPort)
            ).start();

            Logger.getInstance().log("Worker process created, PID: " + process.pid() + ", Port: " + workerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        sendMessageToWorker(gson.toJson(task));
    }

    private void sendMessageToWorker(String message) {
        workerPrintStream.println(message);
        workerPrintStream.flush();
    }

    public boolean isBusy(){
        return isBusy;
    }

    public int getId(){
        return id;
    }
}
