package os.hw2.util;

import os.hw2.Task;

public class Message {
    public static enum Type {
        ASSIGN,
        INTERRUPT,
        CELL_REQUEST,
        CELL_RESPONSE,
        TASKBACK,
        UNLOCK,
        REMOVE_WAITER,
        DEADLOCK_STATE,
        RESULT
    }

    public static enum Sender {
        MASTER,
        STORAGE,
        WORKER
    }

    private Type type;
    private Task task;

    private int taskID;

    private int cellValue, workerID;

    public Task getTask() {
        return task;
    }

    public Type getType(){
        return type;
    }

    public int getCellValue() {
        return cellValue;
    }

    public int getWorkerID() {
        return workerID;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public void setCellValue(int cellValue) {
        this.cellValue = cellValue;
    }

    public void setWorkerID(int workerID) {
        this.workerID = workerID;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", task=" + task +
                ", taskID=" + taskID +
                ", cellValue=" + cellValue +
                ", workerID=" + workerID +
                '}';
    }
}
