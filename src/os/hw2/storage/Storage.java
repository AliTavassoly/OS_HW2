package os.hw2.storage;

import os.hw2.Task;
import os.hw2.util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Storage {
    private int storagePort, numberOfWorkers, numberOfCells;

    private ServerSocket storageServerSocket;

    private MasterHandler masterHandler;

    private WorkerHandler[] workerHandlers;

    private ArrayList<Integer> memory;

    private Semaphore[] semaphores;

    public Storage(int storagePort, int numberOfWorkers) {
        this.storagePort = storagePort;
        this.numberOfWorkers = numberOfWorkers;
        this.workerHandlers = new WorkerHandler[numberOfWorkers];

        memory = new ArrayList<>();
    }

    public void start(){
        try {
            storageServerSocket = new ServerSocket(storagePort);

            masterHandler = new MasterHandler(storageServerSocket, this);

            numberOfCells = masterHandler.initializeMemory(this.memory);

            initializeSemaphores();

            waitForWorkersToConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeSemaphores() {
        semaphores = new Semaphore[numberOfCells];
        for (int i = 0; i < numberOfCells; i++)
            semaphores[i] = new Semaphore(1);
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

    public void cellRequest(Task task, int cellNumber, int workerID) {
        sendCellValueWhenUnlocked(task, cellNumber, workerID);
    }

    private void sendCellValueWhenUnlocked(Task task, int cellNumber, int workerID) {
        new Thread(() -> {
            try {
                semaphores[cellNumber].acquire();
                workerHandlers[workerID].sendCellValue(memory.get(cellNumber));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void unlockCell(Task task) {
        ArrayList<Integer> unlocked = new ArrayList<>();
        for (int cell: task.getInitialCells()) {
            if (!unlocked.contains(cell)) {
                semaphores[cell].release();
                unlocked.add(cell);
            }
        }
    }
}
