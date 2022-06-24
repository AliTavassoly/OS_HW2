package os.hw2.util;

import os.hw2.util.Logger;

import java.util.ArrayList;
import java.util.Arrays;

public class Graph {
    private int n;
    private int cellCount, taskCount; // 0 ... taskCount - 1, taskCount, taskCount + 1 ... taskCount + cellCount - 1

    private ArrayList<Integer>[] adj;

    private boolean hasCycle, mark[];

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
        mark = new boolean[n];
    }

    public void addEdge(int taskNumber, int cellNumber) {
        if (!adj[getTaskNumber(taskNumber)].contains(getCellNumber(cellNumber))) {
            adj[getTaskNumber(taskNumber)].add(getCellNumber(cellNumber));
            Logger.getInstance().log("Graph: Add edge between task " + taskNumber + " and cell number " + cellNumber + " " + this);
        }
    }

    public boolean canAssign(int taskNumber) {
        return !isInCycle(getTaskNumber(taskNumber));
    }

    public void flipEdge(int taskNumber, int cellNumber) {
        adj[getTaskNumber(taskNumber)].remove(Integer.valueOf(getCellNumber(cellNumber)));
        adj[getCellNumber(cellNumber)].add(getTaskNumber(taskNumber));

        Logger.getInstance().log("Graph: Flip edge between task: " + taskNumber + " and cell: " + cellNumber + " " + this);
    }

    private int getCellNumber(int cellNumber) {
        return taskCount + cellNumber;
    }

    private int getTaskNumber(int taskNumber) {
        return taskNumber;
    }

    public void removeEdges(int taskNumber) {
        for (int i = taskCount; i < n; i++) {
            adj[i].remove(Integer.valueOf(getTaskNumber(taskNumber)));
        }

        Logger.getInstance().log("Graph: remove edges of task: " + taskNumber + " " + this);
    }

    private void dfs(int v, int target) {
        mark[v] = true;
        for (int u: adj[v]) {
            if (!mark[u])
                dfs(u, target);
            else if(u == target) {
                hasCycle = true;
            }
        }
    }

    private void clearMarks() {
        hasCycle = false;
        for (int i = 0; i < n; i++) {
            mark[i] = false;
        }
    }

    private boolean isInCycle(int taskNumber) {
        clearMarks();

        dfs(getTaskNumber(taskNumber), getTaskNumber(taskNumber));

        return hasCycle;
    }

    public int getEdgeCount() {
        int ans = 0;
        for (int i = 0; i < n; i++)
            ans += adj[i].size();
        return ans / 2;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "n=" + n +
                ", cellCount=" + cellCount +
                ", taskCount=" + taskCount +
                ", adj=" + Arrays.toString(adj) +
                ", hasCycle=" + hasCycle +
                ", mark=" + Arrays.toString(mark) +
                '}';
    }
}
