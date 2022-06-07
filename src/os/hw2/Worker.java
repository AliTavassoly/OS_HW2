package os.hw2;

import os.hw2.util.Logger;

public class Worker {

    public static void logCreation(){
        long pid = ProcessHandle.current().pid();
        Logger.getInstance().log("Process Start, PID: " + pid);
    }

    public static void main(String[] args) {
        Logger.processName = "Worker";
        logCreation();
    }
}
