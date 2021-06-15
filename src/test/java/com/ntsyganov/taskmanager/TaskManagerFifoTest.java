package com.ntsyganov.taskmanager;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ntsyganov.taskmanager.ProcessPriority.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskManagerFifoTest {

    private final Process process1Med = new Process(1001, MEDIUM);
    private final Process process2Low = new Process(1002, LOW);
    private final Process process3Low = new Process(1003, LOW);
    private final Process process4High = new Process(1004, HIGH);
    private final Process process5Med = new Process(1005, MEDIUM);
    private final Process process10Med = new Process(1010, MEDIUM);

    @Test
    public void add() {
        TaskManagerFifo mgr = new TaskManagerFifo(3);
        assertTrue(mgr.add(new Process(1000, MEDIUM)));
        assertTrue(mgr.add(new Process(1001, MEDIUM)));
        assertTrue(mgr.add(new Process(1002, LOW)));
        assertTrue(mgr.add(new Process(1003, LOW)));
        assertTrue(mgr.add(new Process(1004, MEDIUM)));
        mgr.kill(1002);
        assertTrue(mgr.add(new Process(1005, MEDIUM)));
    }

    @Test
    public void listByInsertionTime() {
        TaskManagerFifo mgr = new TaskManagerFifo(3);
        assertTrue(mgr.listByInsertionTime().isEmpty());
        mgr.add(process1Med);
        mgr.add(process2Low);
        mgr.add(process3Low);
        mgr.add(process4High);
        assertEquals(List.of(process2Low, process3Low, process4High), mgr.listByInsertionTime());
        mgr.kill(process2Low.getPid());
        mgr.add(process5Med);
        assertEquals(List.of(process3Low, process4High, process5Med), mgr.listByInsertionTime());
    }

    @Test
    public void listByPriority() {
        TaskManager mgr = new TaskManagerFifo(3);
        assertTrue(new TaskManagerByPriority(3).listByPriority().isEmpty());
        mgr.add(process1Med);
        mgr.add(process2Low);
        mgr.add(process3Low);
        mgr.add(process4High);
        assertEquals(List.of(process2Low, process3Low, process4High), mgr.listByPriority());
        mgr.kill(process2Low.getPid());
        mgr.add(process5Med);
        assertEquals(List.of(process3Low, process5Med, process4High), mgr.listByPriority());
    }

    @Test
    public void listByPid() {
        TaskManager mgr = new TaskManagerFifo(3);
        assertTrue(mgr.listByPid().isEmpty());
        mgr.add(process10Med);
        mgr.add(process2Low);
        mgr.add(process3Low);
        mgr.add(process4High);
        assertEquals(List.of(process2Low, process3Low, process4High), mgr.listByPid());
        mgr.kill(process3Low.getPid());
        mgr.add(process5Med);
        assertEquals(List.of(process2Low, process4High, process5Med), mgr.listByPid());
    }

    @Test
    public void concurrentOperations() throws InterruptedException {
        TaskManager mgr = new TaskManagerFifo(1000);
        ExecutorService service = Executors.newFixedThreadPool(5);
        service.invokeAll(createTasks(mgr));

        var list = mgr.listByPid();
        assertEquals(1000, list.size());
        assertEquals(4999, list.get(999).getPid());
    }

    private List<Callable<Boolean>> createTasks(TaskManager mgr) {
        return IntStream.range(1, 5000).mapToObj(i -> (Callable<Boolean>) () -> {
            var result = mgr.add(new Process(i, MEDIUM));
            var size = mgr.listByInsertionTime().size();
            if (size % 101 == 0) {
                mgr.kill(size);
            }
            return result;
        }).collect(Collectors.toList());
    }

}