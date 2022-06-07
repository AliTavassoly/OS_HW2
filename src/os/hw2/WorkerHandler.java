package os.hw2;

import java.io.IOException;

public class WorkerHandler {
    private int port;

    public WorkerHandler(){
        try {
            Process process = new ProcessBuilder(
                    Main.commonArgs[0], Main.commonArgs[1], Main.commonArgs[2], "os.hw2.Worker"
            ).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
