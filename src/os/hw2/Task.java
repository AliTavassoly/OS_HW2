package os.hw2;

import os.hw2.util.Logger;

import java.util.ArrayList;

public class Task implements Comparable<Task>{
    public static enum Type {
        SLEEP,
        READ
    }

    private ArrayList<Integer> cells;
    private ArrayList<Long> sleeps;

    private ArrayList<Integer> initialCells;

    private int id, sum = 0;

    private Type currentTask = Type.SLEEP;

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

        this.initialCells = new ArrayList<>(cells);
    }

    public ArrayList getCells() {
        return cells;
    }

    public Type currentTaskType() {
        return currentTask;
    }

    public boolean stopSleep(long sleepTime) {
        if (sleepTime >= sleeps.get(0)) {
            sleptEnough();
            return true;
        } else {
            Logger.getInstance().log("Slept " + sleepTime + " milli seconds");
            sleeps.set(0, sleeps.get(0) - sleepTime);
            return false;
        }
    }

    private void sleptEnough() {
        sleeps.remove(0);
        currentTask = Type.READ;
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
        currentTask = Type.SLEEP;
        sum += cellVale;
    }

    public int getCurrentCell() {
        return cells.get(0);
    }

    public long getCurrentSleep() { return sleeps.get(0); }

    public boolean isFinished() {
        return cells.size() == 0 && sleeps.size() == 0;
    }

    public ArrayList<Integer> getInitialCells() {
        return this.initialCells;
    }

    @Override
    public String toString() {
        return "Task{" +
                "cells=" + cells +
                ", sleeps=" + sleeps +
                ", id=" + id +
                ", sum=" + sum +
                '}';
    }

    @Override
    public int compareTo(Task o) {
        return (int) (sumOfSleeps() - o.sumOfSleeps());
    }
}
