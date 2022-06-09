package os.hw2.worker;

import os.hw2.util.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Worker {
    private int workerPort;

    private ServerSocket serverSocket;
    private PrintStream masterPrintStream;
    private Scanner masterScanner;

    public Worker(int workerPort) {
        this.workerPort = workerPort;
    }

    public void start(){
        try {
            serverSocket = new ServerSocket(workerPort);

            Socket socket = serverSocket.accept();
            masterPrintStream = new PrintStream(socket.getOutputStream());
            masterScanner = new Scanner(socket.getInputStream());

            Logger.getInstance().log("Master connected to Worker");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logCreation(){
        long pid = ProcessHandle.current().pid();
        Logger.getInstance().log("Process start, PID: " + pid);
    }

    public static void main(String[] args) {
        Logger.processName = "Worker";
        logCreation();

        int port = Integer.parseInt(args[0]);

        Worker worker = new Worker(port);
        worker.start();
    }
}
