package com.ntsyganov.taskmanager;

import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.LogManager.getLogger;

public abstract class TaskManagerBounded implements TaskManager {
    private static final Logger LOG = getLogger(TaskManagerBounded.class);
    protected final int maxCapacity;
    protected final Queue<Entry> queue;

    TaskManagerBounded(Queue<Entry> queue, int maxCapacity) {
        // these checks would be done with Preconditions and requireNotNull from Guava in the real code
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be greater than 0, provided: " + maxCapacity);
        }
        this.maxCapacity = maxCapacity;
        this.queue = requireNotNull(queue, "queue");
    }

    /**
     * Implementation must synchronize on queue variable.
     * Verification that the process is unique is out-of-scope (as not explicitly mentioned in the requirements)
     * and should be done by the caller
     *
     * @param process not null
     * @return
     */
    @Override
    public abstract boolean add(Process process);

    @Override
    public List<Process> listByInsertionTime() {
        return list(null);
    }

    @Override
    public List<Process> listByPid() {
        return list(Comparator.comparing(e -> e.getProcess().getPid()));
    }

    @Override
    public List<Process> listByPriority() {
        return list(Comparator.comparing(e -> e.getProcess().getPriority()));
    }

    /**
     * @param comparator if null then sorting is not applied
     * @return
     */
    List<Process> list(Comparator<Entry> comparator) {
        synchronized (queue) {
            if (comparator == null) {
                return queue.stream()
                        .map(Entry::getProcess)
                        .collect(Collectors.toUnmodifiableList());
            } else {
                return queue.stream()
                        .sorted(comparator)
                        .map(Entry::getProcess)
                        .collect(Collectors.toUnmodifiableList());
            }
        }
    }

    @Override
    public boolean kill(int pid) {
        return !kill(process -> process.getPid() == pid).isEmpty();
    }

    @Override
    public List<Integer> kill(ProcessPriority priority) {
        return kill(process -> process.getPriority() == requireNotNull(priority, "priority"));
    }

    // it's likely that in the particular context the performance gain is negligible
    // and the content of this method could be replaced with kill(process -> true);
    @Override
    public List<Integer> killAll() {
        synchronized (queue) {
            var pids = new ArrayList<Integer>();
            for (Entry entry : queue) {
                var process = entry.getProcess();
                pids.add(process.getPid());
                process.kill();
            }
            queue.clear();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Killed {} processes", pids.size());
            }
            return pids;
        }
    }

    private List<Integer> kill(Predicate<Process> processToKill) {
        synchronized (queue) {
            var pids = new ArrayList<Integer>();
            var iter = queue.iterator();
            while (iter.hasNext()) {
                var candidate = iter.next().getProcess();
                if (processToKill.test(candidate)) {
                    iter.remove();
                    pids.add(candidate.getPid());
                    candidate.kill();
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Killed processes with pids: {}", pids);
            }
            return pids;
        }
    }

    @Override
    public String toString() {
        int size;
        synchronized (queue) {
            size = queue.size();
        }
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("maxCapacity=" + maxCapacity)
                .add("queue.size=" + size)
                .toString();
    }

    // In real code this can be replaced by the similar method in Guava
    <T> T requireNotNull(T object, String objectName) {
        if (object == null) {
            throw new IllegalArgumentException("Cannot provide null as the argument: " + objectName);
        }
        return object;
    }

    /**
     * This wrapper for a Process is needed to:
     * a) mark the insertion time (in case it will be needed for the output)
     * b) unify the implementation with TaskManagerByPriority
     */
    public static class Entry {
        private static final AtomicLong SEQ_GENERATOR = new AtomicLong(0);
        private final long seqNum;
        // could be useful for any real usage, but here represents any meta data which can be added by the manager at insertion time
        private final long timestampInMillis;
        private final Process process;

        public Entry(Process process) {
            this.seqNum = SEQ_GENERATOR.getAndIncrement();
            this.timestampInMillis = System.currentTimeMillis();
            this.process = process;
        }

        public Process getProcess() {
            return process;
        }

        public long getSeqNum() {
            return seqNum;
        }

        public long getTimestampInMillis() {
            return timestampInMillis;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return seqNum == entry.seqNum && timestampInMillis == entry.timestampInMillis && Objects.equals(process, entry.process);
        }

        @Override
        public int hashCode() {
            return Objects.hash(seqNum, timestampInMillis, process);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Entry.class.getSimpleName() + "[", "]")
                    .add("seqNum=" + seqNum)
                    .add("tsInMillis=" + timestampInMillis)
                    .add("process=" + process)
                    .toString();
        }
    }
}
