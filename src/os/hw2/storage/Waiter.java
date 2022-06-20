package os.hw2.storage;

public class Waiter {
    private int workerID, taskID;

    public Waiter(int taskID, int workerID) {
        this.taskID = taskID;
        this.workerID = workerID;
    }

    public int getWorkerID() {
        return workerID;
    }

    public int getTaskID() {
        return taskID;
    }
}
