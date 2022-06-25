package os.hw2.master;

import os.hw2.Main;
import os.hw2.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Master {
    private int masterPort, currentSJFTaskIndex = 0;

    private StorageHandler storageHandler;

    private List<WorkerHandler> workerHandlers;

    private List<Task> tasks;

    private final int schedulerSleepTime = 5;
    private int remainsTasks;

    private boolean errorReported = false;

    public Master(int masterPort){
        this.masterPort = masterPort;
        workerHandlers = new LinkedList<>();

        initializeTasks();
    }

    private void initializeTasks() {
        tasks = new ArrayList<>();
        for (int i = 0; i < Main.taskNumber; i++)
            tasks.add(Main.tasks[i]);
        remainsTasks = tasks.size();
        sortTasksIfSJF();
    }

    private void sortTasksIfSJF() {
        if (Main.scheduling == Main.Scheduling.SJF)
            Collections.sort(tasks);
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
        int res = assignTask(findShortestTaskID());
        if (res == -1) // If task can not be assigned because of deadlock
            currentSJFTaskIndex = (currentSJFTaskIndex + 1) % tasks.size();
        else // If task has assigned to some worker, set next task the shortest task
            currentSJFTaskIndex = 0;
    }

    private void assignTaskRR() {
        synchronized (tasks) {
            Task task = tasks.get(0);
            int assignedWorkerID = assignTask(task.getId());
            if (assignedWorkerID != -1)
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
            return tasks.get(currentSJFTaskIndex).getId();
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
        storageHandler.askTaskPermissionFromStorage(taskID);

        boolean answer = storageHandler.getPermissionAnswer();

        if (!answer) {
            if (!errorReported) {
                System.out.println("Deadlock detected");
                errorReported = true;
            }
            switch (Main.scheduling) {
                case RR:
                case FCFS:
                    synchronized (tasks) {
                        tasks.add(tasks.remove(0));
                    }
                    break;
                case SJF:
                    break;
            }
            return -1;
        }

        // TODO: send task to the end of queue in FCFS and RR
        Task task = removeTask(taskID);

        for (WorkerHandler workerHandler: workerHandlers) {
            if (!workerHandler.isBusy()) {
                workerHandler.runTask(task);
                return workerHandler.getId();
            }
        }
        // This line should never be reached
        return -1;
    }

    public void taskResult(Task task) {
        remainsTasks--;

        System.out.println("Task " + task.getId() + " executed successfully with result " + task.getAns());
        storageHandler.unlock(task);

        if (remainsTasks == 0)
            System.exit(0);
    }

    public void taskBack(Task task) {
        synchronized (tasks) {
            tasks.add(task);
        }
        storageHandler.removeFromWaiters(task);
    }

    public void shutDown() {
        storageHandler.shutDown();
        for (WorkerHandler workerHandler: workerHandlers)
            workerHandler.shutDown();
    }
}
