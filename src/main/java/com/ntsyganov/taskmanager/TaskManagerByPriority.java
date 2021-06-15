package com.ntsyganov.taskmanager;

import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static org.apache.logging.log4j.LogManager.getLogger;

public class TaskManagerByPriority extends TaskManagerBounded {
    private static final Logger LOG = getLogger(TaskManagerByPriority.class);

    public final static Comparator<Entry> COMPARATOR_BY_PRIORITY = Comparator.comparing(e -> e.getProcess().getPriority());
    public final static Comparator<Entry> COMPARATOR = COMPARATOR_BY_PRIORITY.thenComparing(Entry::getSeqNum);

    public TaskManagerByPriority(int maxCapacity) {
        super(new PriorityQueue<>(COMPARATOR), maxCapacity);
    }

    @Override
    public boolean add(Process process) {
        requireNotNull(process, "process");
        synchronized (queue) {
            if (queue.size() < maxCapacity) {
                queue.offer(new Entry(process));
                return true;
            } else {
                var candidateForReplacement = queue.peek().getProcess();
                if (candidateForReplacement.getPriority().ordinal() < process.getPriority().ordinal()) {
                    queue.poll();
                    candidateForReplacement.kill();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Killed old process: {} to insert process: {}", candidateForReplacement, process);
                    }
                    queue.offer(new Entry(process));
                    return true;
                }
                return false;
            }
        }
    }

    @Override
    public List<Process> listByInsertionTime() {
        return list(Comparator.comparing(Entry::getSeqNum));
    }

}
