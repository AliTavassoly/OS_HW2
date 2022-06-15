package os.hw2.master;

import os.hw2.Main;
import os.hw2.Message;
import os.hw2.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Master {
    private int masterPort;

    private StorageHandler storageHandler;

    private List<WorkerHandler> workerHandlers;

    private List<Task> tasks;

    private int sleepTime;

    public Master(int masterPort){
        this.masterPort = masterPort;
        workerHandlers = new LinkedList<>();

        initializeTasks();

        sleepTime = 1;
    }

    private void initializeTasks() {
        tasks = new ArrayList<>();
        for (int i = 0; i < Main.taskNumber; i++)
            tasks.add(Main.tasks[i]);
    }

    private void connectToStorage() {
        storageHandler = new StorageHandler(Main.storagePort);
    }

    private void connectToWorkers() {
        for (int i = 0; i < Main.numberOfWorkers; i++){
            workerHandlers.add(new WorkerHandler(Main.firstWorkerPort + i));

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(){
        connectToStorage();

        connectToWorkers();

        runScheduler();
    }

    private void runScheduler() {
        while (true) {
            try {
                assignTasks();
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isThereWorker(){
        for (WorkerHandler workerHandler: workerHandlers) {
            if (!workerHandler.isBusy())
                return true;
        }
        return false;
    }

    private boolean isThereTask() {
        return tasks.size() > 0;
    }

    private void assignTasks() {
        switch (Main.scheduling) {
            case FCFS:
                assignTaskFCFS();
                break;
            case SJF:
                assignTaskSJF();
                break;
            case RR:
                assignTaskRR();
                break;
        }
    }

    private void assignTaskFCFS() {
        if(isThereTask())
            assignTask(tasks.get(0).getId());
    }

    private void assignTaskSJF() {}

    private void assignTaskRR() {

    }

    private Task removeTask(int taskID) {
        for (Task task: tasks) {
            if (task.getId() == taskID) {
                tasks.remove(task);
                return task;
            }
        }
        return null;
    }

    private void assignTask(int taskID) {
        Task task = removeTask(taskID);

        for (WorkerHandler workerHandler: workerHandlers) {
            if (!workerHandler.isBusy()) {
                workerHandler.runTask(task);
            }
        }
    }
}
