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

    private int schedulerSleepTime, remainsTasks;

    public Master(int masterPort){
        this.masterPort = masterPort;
        workerHandlers = new LinkedList<>();

        initializeTasks();

        schedulerSleepTime = 1;
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
                Thread.sleep(20);
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
                Thread.sleep(schedulerSleepTime);
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
        synchronized (tasks) {
            return tasks.size() > 0;
        }
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
        synchronized (tasks) {
            assignTask(tasks.get(0).getId());
        }
    }

    private void assignTaskSJF() {
        assignTask(findShortestTaskID());
    }

    private void assignTaskRR() {
        synchronized (tasks) {
            Task task = tasks.get(0);
            int assignedWorkerID = assignTask(task.getId());
            interrupter(task.getId(), assignedWorkerID);
        }
    }

    private void interrupter(int taskID, int workerID) {
        new Thread(() -> {
            try {
                Thread.sleep(Main.interruptInterval);
                workerHandlers.get(workerID).interrupt(taskID);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private int findShortestTaskID() {
        synchronized (tasks) {
            Task chosenTask = null;
            for (Task task : tasks) {
                if (chosenTask == null || task.sumOfSleeps() < chosenTask.sumOfSleeps()) {
                    chosenTask = task;
                }
            }
            return chosenTask.getId();
        }
    }

    private Task removeTask(int taskID) {
        synchronized (tasks) {
            for (Task task : tasks) {
                if (task.getId() == taskID) {
                    tasks.remove(task);
                    return task;
                }
            }
        }
        return null;
    }

    private int assignTask(int taskID) {
        Task task = removeTask(taskID);

        for (WorkerHandler workerHandler: workerHandlers) {
            if (!workerHandler.isBusy()) {
                workerHandler.runTask(task);
                return workerHandler.getId();
            }
        }
        // This line should never be reached
        return 0;
    }

    public void taskResult(Task task) {
        remainsTasks--;

        System.out.println("Task " + task.getId() + " executed successfully with result " + task.getAns());

        if (remainsTasks == 0)
            System.exit(0);
    }

    public void taskBack(Task task) {
        synchronized (tasks) {
            tasks.add(task);
        }
    }

    public void shutDown() {
        storageHandler.shutDown();
        for (WorkerHandler workerHandler: workerHandlers)
            workerHandler.shutDown();
    }
}
