package com.ntsyganov.taskmanager;

import java.util.List;

/**
 * With a single direct child the interface is not really needed, but could be useful if another implementation is added
 * or for stubbing during testing
 */
public interface TaskManager {

    /**
     * @param process not null
     * @return true if added
     */
    boolean add(Process process);

    /**
     * Lists all managed processes by creation time (i.e. by insertion time as per requirement),
     * where the 1st element is the oldest
     *
     * @return
     */
    List<Process> listByInsertionTime();

    List<Process> listByPid();

    /**
     * Lists all managed processes by priority, where the 1st element is with lowest priority
     *
     * @return
     */
    List<Process> listByPriority();

    /**
     * @param pid
     * @return true if found and killed
     */
    boolean kill(int pid);

    List<Integer> kill(ProcessPriority priority);

    List<Integer> killAll();
}
