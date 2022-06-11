package os.hw2.storage;

public class Waiter {
    int waitingCell, workerID;

    public Waiter(int waitingCell, int workerID) {
        this.waitingCell = waitingCell;
        this.workerID = workerID;
    }

    public int getWaitingCell() {
        return waitingCell;
    }

    public void setWaitingCell(int waitingCell) {
        this.waitingCell = waitingCell;
    }

    public int getWorkerID() {
        return workerID;
    }

    public void setWorkerID(int workerID) {
        this.workerID = workerID;
    }
}
