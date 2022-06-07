package os.hw2;

import os.hw2.util.Logger;

import java.util.Scanner;

public class Main {
    private static Scanner inputScanner;

    public static int commonArgsNumber;
    public static String[] commonArgs;

    public static int masterPort, storagePort;
    public static long numberOfWorkers, interruptInterval;
    public static Scheduling scheduling;
    public static Deadlock deadlock;

    public static int storageLength, taskNumber;
    public static long[] storageData;
    public static long[][] taskSleep, taskIndex;

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
        for (long data: storageData){
            res += data;
            res += " ";
        }
        return res;
    }

    public static void main(String[] args) {
        Logger.processName = "Master";
        Logger.getInstance().log("Master Started");

        input();

        new Master().start();
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
        storageData = new long[storageLength];

        for (int i = 0; i < storageLength; i++)
            storageData[i] = Long.parseLong(listOfData[i]);
    }

    private static void inputTasks() {
        taskIndex = new long[taskNumber][];
        taskSleep = new long[taskNumber][];

        for (int i = 0; i < taskNumber; i++){
            String[] s = inputScanner.nextLine().split(" ");

            int len = s.length;
            taskSleep[i] = new long[(len + 1) / 2];
            taskIndex[i] = new long[len / 2];

            for (int j = 0; j < len; j++) {
                if (j % 2 == 0) {
                    taskSleep[i][j / 2] = Long.parseLong(s[j]);
                } else {
                    taskIndex[i][j / 2] = Long.parseLong(s[j]);
                }
            }
        }
    }

    private static void input() {
        inputScanner = new Scanner(System.in);

        inputArgs();

        masterPort = Integer.parseInt(inputScanner.nextLine());
        numberOfWorkers = Long.parseLong(inputScanner.nextLine());

        Scheduling scheduling = Scheduling.valueOf(inputScanner.nextLine());

        if (scheduling == Scheduling.RR)
            interruptInterval = Long.parseLong(inputScanner.nextLine());

        Deadlock deadlock = Deadlock.valueOf(inputScanner.nextLine());

        storagePort = Integer.parseInt(inputScanner.nextLine());

        inputStorageData();

        taskNumber = Integer.parseInt(inputScanner.nextLine());

        inputTasks();

//        System.out.println(scheduling);
//        System.out.println(deadlock);
//        System.out.println(taskNumber);
//        System.out.println(interruptInterval);
//        System.out.println(Arrays.toString(storageData));
//        System.out.println(Arrays.deepToString(taskSleep));
//        System.out.println(Arrays.deepToString(taskIndex));
    }
}
