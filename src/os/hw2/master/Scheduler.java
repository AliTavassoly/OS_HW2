package os.hw2.master;

import os.hw2.Main;
import os.hw2.Task;

import java.util.ArrayList;

public class Scheduler {
    private Main.Scheduling scheduling;
    private Main.Deadlock deadlock;

    public Scheduler(Main.Scheduling scheduling, Main.Deadlock deadlock) {
        this.scheduling = scheduling;
        this.deadlock = deadlock;
    }

    public void schedule(ArrayList<Task> tasks) {
        if (!Main.master.isThereWorker() || tasks.size() == 0)
            return;

        if (scheduling == Main.Scheduling.FCFS)
            scheduleFCFS(tasks);
        if (scheduling == Main.Scheduling.SJF)
            scheduleSJF(tasks);
        if (scheduling == Main.Scheduling.RR)
            scheduleRR(tasks);
    }

    private void scheduleFCFS(ArrayList<Task> tasks) {
        Task task = tasks.get(0);
        Main.master.assignTask(task.getId());
    }

    private void scheduleSJF(ArrayList<Task> tasks) { }

    private void scheduleRR(ArrayList<Task> tasks) {}
}
