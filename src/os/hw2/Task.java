package os.hw2;

import os.hw2.util.Logger;

import java.util.ArrayList;

public class Task {
    private ArrayList<Integer> cells;
    private ArrayList<Long> sleeps;

    private int id, sum = 0;

    private long lastStartedSleep;

    public Task(ArrayList<Integer> arrayList, int id) {
        this.cells = new ArrayList<>();
        this.sleeps = new ArrayList<>();
        this.id = id;

        int isSleep = 1;
        for (int i = 0; i < arrayList.size(); i++) {
            if (isSleep == 1) {
                sleeps.add((long) arrayList.get(i));
            } else {
                cells.add(arrayList.get(i));
            }

            isSleep = (isSleep + 1) % 2;
        }
    }

    public Task(int[] cellsAndSleeps, int id) {
        this.cells = new ArrayList<>();
        this.sleeps = new ArrayList<>();
        this.id = id;

        int isSleep = 1;
        for (int i = 0; i < cellsAndSleeps.length; i++) {
            if (isSleep == 1) {
                sleeps.add((long) cellsAndSleeps[i]);
            } else {
                cells.add(cellsAndSleeps[i]);
            }

            isSleep = (isSleep + 1) % 2;
        }
    }

    public ArrayList getCells() {
        return cells;
    }

    public Integer stopSleep() {
        long sleepTime = calculateSleepTime();
        if (sleepTime > sleeps.get(0)) {
            sleeps.remove(0);
            if (cells.size() == 0)
                return -2;
            return cells.get(0);
        } else {
            sleeps.set(0, sleeps.get(0) - sleepTime);
            return -1;
        }
    }

    public long startSleep() {
        lastStartedSleep = System.currentTimeMillis();
        return sleeps.get(0);
    }

    private long calculateSleepTime() {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastStartedSleep;
    }

    public long sumOfSleeps() {
        long sum = 0;
        for (long x: sleeps) {
            sum += x;
        }
        return sum;
    }

    public int getAns() {
        return sum;
    }

    public int getId(){
        return id;
    }

    public void newCellValue(Integer cellVale) {
        cells.remove(0);
        sum += cellVale;
    }

    public boolean isFinished() {
        return cells.size() == 0 && sleeps.size() == 0;
    }

    @Override
    public String toString() {
        return "Task{" +
                "cells=" + cells +
                ", sleeps=" + sleeps +
                ", id=" + id +
                ", sum=" + sum +
                ", lastStartedSleep=" + lastStartedSleep +
                '}';
    }
}
