package os.hw2.storage;

import os.hw2.util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Storage {
    private int storagePort, numberOfWorkers;

    private ServerSocket storageServerSocket;

    private MasterHandler masterHandler;

    private WorkerHandler[] workerHandlers;

    private ArrayList<Integer> memory;

    public Storage(int storagePort, int numberOfWorkers) {
        this.storagePort = storagePort;
        this.numberOfWorkers = numberOfWorkers;
        this.workerHandlers = new WorkerHandler[numberOfWorkers];

        memory = new ArrayList<>();
    }

    public void start(){
        try {
            storageServerSocket = new ServerSocket(storagePort);

            masterHandler = new MasterHandler(storageServerSocket);

            masterHandler.initializeMemory(this.memory);

            waitForWorkersToConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForWorkersToConnect() {
        new Thread(() -> {
            for (int i = 0; i < numberOfWorkers; i++){
                WorkerHandler workerHandler = new WorkerHandler(storageServerSocket, this);
                workerHandlers[workerHandler.getWorkerID()] = workerHandler;
            }
        }).start();
    }

    public static void logCreation(){
        long pid = ProcessHandle.current().pid();
        Logger.getInstance().log("Process start, PID: " + pid);
    }

    public static void main(String[] args) {
        Logger.processName = "Storage";
        logCreation();

        int port = Integer.parseInt(args[0]);
        int numberOfWorkers = Integer.parseInt(args[1]);

        Storage storage = new Storage(port, numberOfWorkers);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.getInstance().log("Bye bye!");
            storage.shutDown();
        }));
        storage.start();
    }

    private void shutDown() {
        try {
            storageServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cellRequest(int cellNumber, int workerID) {
        // TODO: check if cell number is available
        workerHandlers[workerID].sendCellValue(memory.get(cellNumber));
    }
}
