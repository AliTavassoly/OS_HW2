package os.hw2;

public class Message {
    public static enum Type {
        ASSIGN,
        INTERRUPT,
        TASKBACK
    }

    private Type type;
    private Task task;

    public Message (Type type) {
        this.type = type;
    }

    public Message (Type type, Task task) {
        this.type = type;
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public Type getType(){
        return type;
    }
}
