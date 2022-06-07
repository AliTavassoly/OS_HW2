package os.hw2;

import os.hw2.util.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Storage {
    private int storagePort;

    private ServerSocket serverSocket;
    private PrintStream masterPrintStream;
    private Scanner masterScanner;

    private ArrayList memory;

    public Storage(int storagePort) {
        this.storagePort = storagePort;
        memory = new ArrayList();
    }

    public void start(){
        try {
            serverSocket = new ServerSocket(storagePort);

            Socket socket = serverSocket.accept();
            masterPrintStream = new PrintStream(socket.getOutputStream());
            masterScanner = new Scanner(socket.getInputStream());

            Logger.getInstance().log("Master connected to Storage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Logger.processName = "Storage";
        Logger.getInstance().log("Storage Process Started");

        int port = Integer.parseInt(args[0]);

        Storage storage = new Storage(port);
        storage.start();
    }

}
