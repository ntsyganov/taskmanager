package com.ntsyganov.taskmanager;

import java.util.LinkedList;

/**
 * Bounded, but not blocking task manager.
 * The implementation is not optimized for any particular scenario as it's likely that for the given context (managing OS processes)
 * the simplicity and unification of the code is more important than the minimal gain in performance
 */
public class TaskManagerRejecting extends TaskManagerBounded {

    public TaskManagerRejecting(int maxCapacity) {
        super(new LinkedList<>(), maxCapacity);
    }

    @Override
    public boolean add(Process process) {
        requireNotNull(process, "process");
        synchronized (queue) {
            if (queue.size() < maxCapacity) {
                queue.offer(new Entry(process));
                return true;
            } else {
                return false;
            }
        }
    }

}
