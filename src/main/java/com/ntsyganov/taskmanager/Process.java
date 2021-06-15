package com.ntsyganov.taskmanager;

import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.StringJoiner;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Process {
    private static final Logger LOG = getLogger(Process.class);

    private final int pid;
    private final ProcessPriority priority;

    public Process(int pid, ProcessPriority priority) {
        this.pid = pid;
        this.priority = priority;
    }

    /**
     * Kills the underlying OS process. Should be called only by the Task Manager
     */
    public void kill() {
        LOG.info("Native killing the process with pid: {}", pid);
    }

    public int getPid() {
        return pid;
    }

    public ProcessPriority getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Process process = (Process) o;
        return pid == process.pid && priority == process.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid, priority);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Process.class.getSimpleName() + "[", "]")
                .add("pid=" + pid)
                .add("priority=" + priority)
                .toString();
    }
}
