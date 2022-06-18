package os.hw2.master;

import os.hw2.Main;
import os.hw2.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Master {
    private int masterPort;

    private StorageHandler storageHandler;

    private List<WorkerHandler> workerHandlers;

    private List<Task> tasks;

    private int sleepTime, remainsTasks;

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
        remainsTasks = tasks.size();
    }

    private void connectToStorage() {
        storageHandler = new StorageHandler(Main.storagePort);
    }

    private void connectToWorkers() {
        for (int i = 0; i < Main.numberOfWorkers; i++){
            workerHandlers.add(new WorkerHandler(Main.firstWorkerPort + i, this));

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
        if (!isThereWorker() || !isThereTask())
            return;

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
        assignTask(tasks.get(0).getId());
    }

    private void assignTaskSJF() {
        assignTask(findShortestTaskID());
    }

    private void assignTaskRR() {

    }

    private int findShortestTaskID() {
        Task chosenTask = null;
        for(Task task: tasks) {
            if (chosenTask == null || task.sumOfSleeps() < chosenTask.sumOfSleeps()) {
                chosenTask = task;
            }
        }
        return chosenTask.getId();
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
                return;
            }
        }
    }

    public void shutDown() {
        storageHandler.shutDown();
        for (WorkerHandler workerHandler: workerHandlers)
            workerHandler.shutDown();
    }

    public void taskResult(Task task) {
        remainsTasks--;

        System.out.println("Task " + task.getId() + " executed successfully with result " + task.getAns());

        if (remainsTasks == 0)
            System.exit(0);
    }
}
