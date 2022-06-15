package os.hw2;

import java.util.ArrayList;

public class Task {
    private ArrayList<Integer> cells;
    private ArrayList<Integer> sleeps;

    private int id, sum = 0;

    private boolean remainSleep = false;

    public Task(ArrayList<Integer> arrayList, int id) {
        this.cells = new ArrayList<>();
        this.sleeps = new ArrayList<>();
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

    public ArrayList getCells() {
        return cells;
    }

    public void stopSleeping() {
        if () {
            doneSleeping();
        }
    }

    public void doneSleeping() {
        remainSleep = false;
    }

    public void startSleeping() {
        remainSleep = true;
    }

    public int getCurrentCell() {
        int tmp = cells.remove(0);
        return tmp;
    }

    public int getAns() {
        return sum;
    }

    public int getId(){
        return id;
    }

    // TODO: toString function need to be implemented
}
