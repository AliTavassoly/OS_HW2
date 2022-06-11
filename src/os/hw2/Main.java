package os.hw2;

import os.hw2.master.Master;
import os.hw2.master.Task;
import os.hw2.util.Logger;

import java.util.Scanner;

public class Main {
    private static Scanner inputScanner;

    public static int commonArgsNumber;
    public static String[] commonArgs;

    public static int masterPort, storagePort, firstWorkerPort = 12345;
    public static int numberOfWorkers, interruptInterval;
    public static Scheduling scheduling;
    public static Deadlock deadlock;

    public static int storageLength, taskNumber;
    public static int[] storageData;
    public static int[][] cellsAndSleeps;

    public static Task[] tasks;

    public static enum Scheduling {
        FCFS,
        SJF,
        RR
    }

    public static enum Deadlock {
        PREVENT,
        DETECT,
        NONE
    }

    public static String getStorageDataString(){
        String res = "";
        for (int data: storageData){
            res += data;
            res += " ";
        }
        return res;
    }

    public static void main(String[] args) {
        Logger.processName = "Master";
        Logger.getInstance().log("Process start");

        input();

        new Master(Main.masterPort).start();
    }

    private static void inputArgs(){
        commonArgsNumber = Integer.parseInt(inputScanner.nextLine());

        commonArgs = new String[commonArgsNumber];
        for (int i = 0; i < commonArgsNumber; i++){
            commonArgs[i] = inputScanner.nextLine();
        }
    }

    private static void inputStorageData() {
        String[] listOfData = inputScanner.nextLine().split(" ");
        storageLength = listOfData.length;
        storageData = new int[storageLength];

        for (int i = 0; i < storageLength; i++)
            storageData[i] = Integer.parseInt(listOfData[i]);
    }

    private static void inputTasks() {
        cellsAndSleeps = new int[taskNumber][];
        tasks = new Task[taskNumber];

        for (int i = 0; i < taskNumber; i++){
            String[] s = inputScanner.nextLine().split(" ");

            int len = s.length;
            cellsAndSleeps[i] = new int[len];

            for (int j = 0; j < len; j++) {
                cellsAndSleeps[i][j] = Integer.parseInt(s[j]);
            }

            tasks[i] = new Task(cellsAndSleeps[i]);
        }
    }

    private static void input() {
        inputScanner = new Scanner(System.in);

        inputArgs();

        masterPort = Integer.parseInt(inputScanner.nextLine());
        numberOfWorkers = Integer.parseInt(inputScanner.nextLine());

        Scheduling scheduling = Scheduling.valueOf(inputScanner.nextLine());

        if (scheduling == Scheduling.RR)
            interruptInterval = Integer.parseInt(inputScanner.nextLine());

        Deadlock deadlock = Deadlock.valueOf(inputScanner.nextLine());

        storagePort = Integer.parseInt(inputScanner.nextLine());

        inputStorageData();

        taskNumber = Integer.parseInt(inputScanner.nextLine());

        inputTasks();
    }
}
