package os.hw2;

import java.util.ArrayList;

public class Task {
    private ArrayList<Integer> cells;
    private ArrayList<Integer> sleeps;

    private boolean isCurrentSleep;

    private int id, sum = 0;

    public Task(ArrayList<Integer> arrayList, int id) {
        this.cells = new ArrayList<>();
        this.sleeps = new ArrayList<>();
        this.isCurrentSleep = true;
        this.id = id;

        int isSleep = 1;
        for (int i = 0; i < arrayList.size(); i++) {
            if (isSleep == 1) {
                sleeps.add(arrayList.get(i));
            } else {
                cells.add(arrayList.get(i));
            }

            isSleep = (isSleep + 1) % 2;
        }
    }

    public Task(int[] cellsAndSleeps, int id) {
        this.cells = new ArrayList<>();
        this.sleeps = new ArrayList<>();
        this.isCurrentSleep = true;
        this.id = id;

        int isSleep = 1;
        for (int i = 0; i < cellsAndSleeps.length; i++) {
            if (isSleep == 1) {
                sleeps.add(cellsAndSleeps[i]);
            } else {
                cells.add(cellsAndSleeps[i]);
            }

            isSleep = (isSleep + 1) % 2;
        }
    }

    int getCurrentCell() {
        return cells.get(0);
    }

    int getCurrentSleep() {
        return sleeps.get(0);
    }

    void removeCurrentCell() {
        cells.remove(0);
    }

    void removeCurrentSleep() {
        sleeps.remove(0);
    }

    public ArrayList getCells() {
        return cells;
    }

    public ArrayList getSleeps() {
        return sleeps;
    }

    public boolean isCurrentSleep() {
        return isCurrentSleep;
    }

    public int getAns() {
        return sum;
    }

    public int getId(){
        return id;
    }
}
