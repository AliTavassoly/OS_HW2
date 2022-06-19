package os.hw2.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import os.hw2.util.Message;
import os.hw2.util.Logger;
import os.hw2.util.MyGson;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class WorkerHandler {
    private PrintStream workerPrintStream;
    private Scanner workerScanner;

    private int workerID;

    private Storage storage;

    public WorkerHandler(ServerSocket storageServerSocket, Storage storage) {
        this.storage = storage;

        connectToWorker(storageServerSocket);

        listenToWorker();
    }

    private void connectToWorker(ServerSocket storageServerSocket) {
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

    private void listenToWorker() {
        Thread thread = new Thread(() -> {
            while (true) {
                Message message = MyGson.getGson().fromJson(workerScanner.nextLine(), Message.class);

                Logger.getInstance().log("New message from worker: " + message);

                storage.newMessageFromWorker(message);
            }
        });
        thread.start();
    }

    public void sendCellValue(Integer cellValue) {
        Message message = new Message(Message.Type.CELLRESPONSE, Message.Sender.STORAGE, cellValue, workerID);
        sendMessage(message);
    }

    private void sendMessage(Message message) {
        workerPrintStream.println(MyGson.getGson().toJson(message));
        workerPrintStream.flush();
    }
}
