package os.hw2.storage;

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
        new Thread(() -> {
            while (true) {
                Message message = MyGson.getGson().fromJson(workerScanner.nextLine(), Message.class);

                Logger.getInstance().log("New message from worker: " + message);

                newMessageFromWorker(message);
            }
        }).start();
    }

    public void sendCellValue(Integer cellValue) {
        Message message = new Message();
        message.setType(Message.Type.CELL_RESPONSE);
        message.setCellValue(cellValue);
        message.setWorkerID(workerID);

        sendMessage(message);
    }

    private void sendMessage(Message message) {
        workerPrintStream.println(MyGson.getGson().toJson(message));
        workerPrintStream.flush();
    }

    public void newMessageFromWorker(Message message) {
        switch (message.getType()) {
            case CELL_REQUEST:
                storage.cellRequest(message.getTask(), message.getCellValue(), message.getWorkerID());
                break;
        }
    }
}
