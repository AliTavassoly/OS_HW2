package os.hw2;

public class Message {
    public static enum Type {
        ASSIGN,
        INTERRUPT,
        TASKBACK
    }

    public static enum Sender {
        MASTER,
        STORAGE,
        WORKER
    }

    private Type type;
    private Task task;
    private Sender sender;

    public Message (Type type) {
        this.type = type;
    }

    public Message (Type type, Sender sender, Task task) {
        this.type = type;
        this.task = task;
        this.sender = sender;
    }

    public Task getTask() {
        return task;
    }

    public Type getType(){
        return type;
    }

    public Sender getSender() { return sender;}
}
