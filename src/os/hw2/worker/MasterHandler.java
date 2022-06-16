package os.hw2.worker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import os.hw2.Message;
import os.hw2.util.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MasterHandler {
    private ServerSocket masterServerSocket;
    private PrintStream masterPrintStream;
    private Scanner masterScanner;

    private GsonBuilder gsonBuilder;
    private Gson gson;

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

    private void createGson() {
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    public void startListening() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Message message = gson.fromJson(masterScanner.nextLine(), Message.class);
                    worker.newMessageFromMaster(message);
                }
            }
        });
        thread.start();
    }
}
