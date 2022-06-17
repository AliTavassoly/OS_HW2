package os.hw2.storage;

import os.hw2.util.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class WorkerHandler {
    private PrintStream workerPrintStream;
    private Scanner workerScanner;

    private int workerID;

    public WorkerHandler(ServerSocket storageServerSocket) {
        try {
            Socket socket = storageServerSocket.accept();
            workerPrintStream = new PrintStream(socket.getOutputStream());
            workerScanner = new Scanner(socket.getInputStream());

            workerID = Integer.parseInt(workerScanner.nextLine());

            Logger.getInstance().log("Worker with ID: " + workerID + " connected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getWorkerID() {
        return workerID;
    }
}
