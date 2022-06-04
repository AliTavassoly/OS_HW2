import java.util.Arrays;
import java.util.Scanner;

public class Master {
    private static Scanner inputScanner;

    private static int commonArgsNumber;
    private static String[] commonArgs;

    private static long masterPort, storagePort, numberOfWorkers, interruptInterval;
    private static Scheduling scheduling;
    private static Deadlock deadlock;

    private static int storageLength, taskNumber;
    private static long[] storageData;
    private static long[][] taskSleep, taskIndex;

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

    public static void main(String[] args) {
        input();

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

        masterPort = Long.parseLong(inputScanner.nextLine());
        numberOfWorkers = Long.parseLong(inputScanner.nextLine());

        Scheduling scheduling = Scheduling.valueOf(inputScanner.nextLine());

        if (scheduling == Scheduling.RR)
            interruptInterval = Long.parseLong(inputScanner.nextLine());

        Deadlock deadlock = Deadlock.valueOf(inputScanner.nextLine());

        storagePort = Long.parseLong(inputScanner.nextLine());

        inputStorageData();

        taskNumber = Integer.parseInt(inputScanner.nextLine());

        inputTasks();

        System.out.println(scheduling);
        System.out.println(deadlock);
        System.out.println(taskNumber);
        System.out.println(interruptInterval);
        System.out.println(Arrays.toString(storageData));
        System.out.println(Arrays.deepToString(taskSleep));
        System.out.println(Arrays.deepToString(taskIndex));
    }
}
