package os.hw2.master;

import os.hw2.Main;

import java.util.LinkedList;
import java.util.List;

public class Master {
    private int masterPort;

    private StorageHandler storageHandler;

    private List<WorkerHandler> workerHandlers;

    public Master(int masterPort){
        this.masterPort = masterPort;
        workerHandlers = new LinkedList<>();
    }

    private void connectToStorage() {
        storageHandler = new StorageHandler(Main.storagePort);
    }

    private void connectToWorkers() {
        for (int i = 0; i < Main.numberOfWorkers; i++){
            workerHandlers.add(new WorkerHandler(Main.firstWorkerPort + i));
        }
    }

    private void initializeStorage(){
        // TODO
    }

    public void start(){
        connectToStorage();

        initializeStorage();

        connectToWorkers();
    }
}
