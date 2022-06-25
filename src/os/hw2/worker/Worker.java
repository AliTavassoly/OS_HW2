package os.hw2.worker;

import os.hw2.util.Message;
import os.hw2.Task;
import os.hw2.util.Logger;
import os.hw2.util.MyGson;

public class Worker {
    private int workerPort, storagePort;

    private MasterHandler masterHandler;

    private StorageHandler storageHandler;

    private int id;

    private Integer cellValue = null;

    private Task task;

    private Thread taskThread;

    public Worker(int workerPort, int storagePort, int id) {
        this.workerPort = workerPort;
        this.storagePort = storagePort;
        this.id = id;

        logCreation();
    }

    public void start(){
        connectToMaster();

        connectToStorage();
    }

    private void connectToStorage() {
        storageHandler = new StorageHandler(id, storagePort, this);
        storageHandler.startListening();
    }

    private void connectToMaster() {
        masterHandler = new MasterHandler(workerPort, this);
        masterHandler.startListening();
    }

    public void logCreation(){
        long pid = ProcessHandle.current().pid();
        Logger.getInstance().log("Process start, PID: " + pid + ", worker ID: " + id);
    }

    // Returns -1 if sleep is interrupted, -2 if task is finished and cell value otherwise
    private boolean runSleepingSubTask() {
        long sleepTime = System.currentTimeMillis();
        Logger.getInstance().log("Start sleep, Sleep time: " + sleepTime + " task ID: " + task.getCurrentSleep());

        synchronized (task) {
            try {
                task.wait(task.getCurrentSleep() + 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        sleepTime = System.currentTimeMillis() - sleepTime;

        Logger.getInstance().log("Stop sleep");
        return task.stopSleep(sleepTime);
    }

    public void runTask(Task task) {
        Logger.getInstance().log("Start running task with ID: " + task.getId());
        taskThread = new Thread(() -> {
            this.task = task;
            while (true) {
                if (task.isFinished()) {
                    returnTaskResult();
                    break;
                }

                if (task.currentTaskType() == Task.Type.SLEEP) {
                    boolean sleepState = runSleepingSubTask();

                    if (!sleepState) {
                        returnIncompleteTask();
                        break;
                    }
                } else {
                    int cellNumber = task.getCurrentCell();
                    boolean cellRequestState = requestForStorageCell(cellNumber);

                    if (!cellRequestState) {
                        returnIncompleteTask();
                        break;
                    }
                }
            }
        });
        taskThread.start();
    }

    public void cellResponse(Message message) {
        this.cellValue = message.getCellValue();
        synchronized (task) {
            task.notify();
        }
    }

    private boolean requestForStorageCell(int cellNumber) {
        try {
            storageHandler.getCellValue(cellNumber, task);
            Logger.getInstance().log("Start waiting for cell " + cellNumber + " ...");

            synchronized (task) {
                task.wait();
            }

            if (this.cellValue == null) // If task is interrupted
                return false;

            // If task did not interrupt
            task.newCellValue(this.cellValue);
            cellValue = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void returnIncompleteTask() {
        Message message = new Message();
        message.setType(Message.Type.TASKBACK);
        message.setTask(task);

        masterHandler.sendMessage(message);
    }

    private void returnTaskResult() {
        Message message = new Message();
        message.setType(Message.Type.RESULT);
        message.setTask(task);

        masterHandler.sendMessage(message);
    }

    public static void main(String[] args) {
        int workerPort = Integer.parseInt(args[0]);
        int storagePort = Integer.parseInt(args[1]);
        int id = Integer.parseInt(args[2]);

        Logger.processName = "Worker " + id;

        Logger.getInstance();
        MyGson.testGson();

        Worker worker = new Worker(workerPort, storagePort, id);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.getInstance().log("Bye bye!");
            worker.shutDown();
        }));
        worker.start();
    }

    public void interruptTask(int taskID) {
        synchronized (task) {
            if (task != null && task.getId() == taskID) {
                task.notify();
            }
        }
    }

    private void shutDown() {
        masterHandler.shutDown();
        storageHandler.shutDown();
    }
}
