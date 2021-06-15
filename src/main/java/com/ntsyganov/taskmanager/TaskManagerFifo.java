package com.ntsyganov.taskmanager;

import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

import static org.apache.logging.log4j.LogManager.getLogger;

public class TaskManagerFifo extends TaskManagerBounded {
    private static final Logger LOG = getLogger(TaskManagerFifo.class);

    public TaskManagerFifo(int maxCapacity) {
        super(new LinkedList<>(), maxCapacity);
    }

    @Override
    public boolean add(Process process) {
        requireNotNull(process, "process");
        synchronized (queue) {
            if (queue.size() >= maxCapacity) {
                var oldProcess = queue.poll().getProcess();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Killed old process: {} to insert process: {}", oldProcess, process);
                }
                oldProcess.kill();
            }
            queue.offer(new Entry(process));
            return true;
        }
    }

}
