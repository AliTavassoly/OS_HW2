package os.hw2.graph;

import java.util.ArrayList;

public class Graph {
    private int n;
    private int cellCount, taskCount; // 0 ... taskCount - 1, taskCount, taskCount + 1 ... taskCount + cellCount - 1

    private ArrayList<Integer>[] adj;

    private boolean hasCycle, white[], black[];

    public Graph(int cellCount, int taskCount) {
        this.cellCount = cellCount;
        this.taskCount = taskCount;

        n = cellCount + taskCount;

        initializeAdj();

        initializeMarks();
    }

    private void initializeAdj() {
        adj = new ArrayList[cellCount + taskCount];
        for(int i = 0; i < n; i++) {
            adj[i] = new ArrayList<Integer>();
        }
    }

    private void initializeMarks() {
        white = new boolean[n];
        black = new boolean[n];
    }

    public int getCellCount() {
        return cellCount;
    }

    public void setCellCount(int cellCount) {
        this.cellCount = cellCount;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public void requestCell(int taskNumber, int cellNumber) {
        adj[taskNumber].add(taskCount + cellNumber);
    }

    public void allocateCell(int taskNumber, int cellNumber) {
        adj[taskCount + cellNumber].add(taskNumber);
    }

    public boolean canAllocate(int taskNumber, int cellNumber) {
        if (cellHasOutEdge(cellNumber)) // TODO: can be checked in storage
            return false;
        flipEdge(getTaskNumber(taskNumber), getCellNumber(cellNumber));
        if (hasCycle()) {
            flipEdge(getCellNumber(cellNumber), getTaskNumber(taskNumber));
            return false;
        }
        flipEdge(getCellNumber(cellNumber), getTaskNumber(taskNumber));
        return true;
    }

    private boolean cellHasOutEdge(int cellNumber) {
        return adj[cellNumber].size() != 0;
    }

    public void flipEdge(int i, int j) {
        adj[i].remove((Integer) j);
        adj[j].add(i);
    }

    private int getCellNumber(int cellNumber) {
        return taskCount + cellNumber;
    }

    private int getTaskNumber(int taskNumber) {
        return taskNumber;
    }

    private void dfs(int v) {
        white[v] = true;
        black[v] = true;
        for (int u: adj[v]) {
            if (black[u])
                hasCycle = true;
            if (!white[u])
                dfs(u);
        }
        black[v] = false;
    }

    private void clearMarks() {
        hasCycle = false;
        for (int i = 0; i < n; i++) {
            white[i] = false;
            black[i] = false;
        }
    }

    private boolean hasCycle() {
        clearMarks();

        for (int i = 0; i < n; i++)
            if(!white[i])
                dfs(i);

        return hasCycle;
    }
}
