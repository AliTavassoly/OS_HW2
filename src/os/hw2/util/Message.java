package os.hw2.util;

import os.hw2.Task;

public class Message {
    public static enum Type {
        ASSIGN,
        INTERRUPT,
        CELLREQUEST,
        CELLRESPONSE,
        TASKBACK,
        RESULT
    }

    public static enum Sender {
        MASTER,
        STORAGE,
        WORKER
    }

    private Type type;
    private Task task;
    private Sender sender;

    private int taskID;

    private int cellValue, workerID;

    public Message (Type type, Sender sender, int cellValue, int workerID) {
        this.type = type;
        this.sender = sender;
        this.cellValue = cellValue;
        this.workerID = workerID;
    }

    public Message (Type type, Sender sender, Task task, int workerID) {
        this.type = type;
        this.task = task;
        this.sender = sender;
        this.workerID = workerID;
    }

    public Message (Type type, Sender sender, Task task) {
        this.type = type;
        this.task = task;
        this.sender = sender;
    }

    public Message (Type type, Sender sender, int taskID) {
        this.type = type;
        this.sender = sender;
        this.taskID = taskID;
    }

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

    public Sender getSender() { return sender;}

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", task=" + task +
                ", sender=" + sender +
                ", cellValue=" + cellValue +
                ", workerID=" + workerID +
                '}';
    }
}
