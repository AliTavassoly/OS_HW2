package os.hw2.storage;

import os.hw2.Task;
import os.hw2.util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Storage {
    private int storagePort, numberOfWorkers, numberOfCells;

    private ServerSocket storageServerSocket;

    private MasterHandler masterHandler;

    private WorkerHandler[] workerHandlers;

    private ArrayList<Integer> memory;

    private ArrayList<Waiter> waiters[];

    private Integer[] locks;

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
        waiters = new ArrayList[numberOfCells];
        locks = new Integer[numberOfCells];

        for (int i = 0; i < numberOfCells; i++) {
            waiters[i] = new ArrayList<Waiter>();
            locks[i] = -1;
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

    public void cellRequest(Task task, int cellNumber, int workerID) {
        sendCellValue(task, cellNumber, workerID);
    }

    private synchronized void sendCellValue(Task task, int cellNumber, int workerID) {
        if(locks[cellNumber] == -1 || locks[cellNumber] == task.getId()) {
            locks[cellNumber] = task.getId();
            workerHandlers[workerID].sendCellValue(memory.get(cellNumber));
        } else {
            waiters[cellNumber].add(new Waiter(task.getId(), workerID));
        }
    }

    private void unlockCell(int cell){
        if (waiters[cell].size() > 0) {
            Waiter waiter = waiters[cell].remove(0);
            locks[cell] = waiter.getTaskID();
            workerHandlers[waiter.getWorkerID()].sendCellValue(memory.get(cell));
        }
    }

    public synchronized void unlockTask(Task task) {
        ArrayList<Integer> unlocked = new ArrayList<>();
        for (int cell: task.getInitialCells()) {
            if (!unlocked.contains(cell)) {
                locks[task.getId()] = -1;
                unlockCell(cell);
                unlocked.add(cell);
            }
        }
    }

    private void shutDown() {
        try {
            storageServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeWaitersWithTaskID(ArrayList<Waiter> list, int taskID) {
        ArrayList<Waiter> shouldRemove = new ArrayList<>();
        for (Waiter waiter: list) {
            if(waiter.getTaskID() == taskID)
                shouldRemove.add(waiter);
        }

        for (Waiter waiter: shouldRemove) {
            list.remove(waiter);
        }
    }

    public synchronized void removeWaiters(Task task) {
        for (int i = 0; i < waiters.length; i++)
            removeWaitersWithTaskID(waiters[i], task.getId());
    }
}
