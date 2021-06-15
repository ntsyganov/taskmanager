package com.ntsyganov.taskmanager;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static com.ntsyganov.taskmanager.ProcessPriority.*;
import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerBoundedTest {

    @Test
    public void negativeOrZeroCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new TaskManagerByPriority(-1));
        assertThrows(IllegalArgumentException.class, () -> new TaskManagerByPriority(0));
    }

    @Test
    public void addWithNull() {
        assertThrows(IllegalArgumentException.class, () -> new TaskManagerByPriority(10).add(null));
        new TaskManagerByPriority(10).add(new Process(1000, MEDIUM));
    }

    @Test
    public void killById() {
        TaskManager mgr = new TaskManagerByPriority(3);
        assertFalse(mgr.kill(1000));
        mgr.add(new Process(1001, MEDIUM));
        assertTrue(mgr.kill(1001));
        mgr.add(new Process(1001, LOW));
        mgr.add(new Process(1002, LOW));
        mgr.add(new Process(1003, LOW));
        assertTrue(mgr.kill(1001));
        assertFalse(mgr.kill(1001));
    }

    @Test
    public void killByPriority() {
        TaskManager mgr = new TaskManagerByPriority(3);
        assertTrue(mgr.kill(MEDIUM).isEmpty());
        mgr.add(new Process(1000, MEDIUM));
        assertEquals(List.of(1000), mgr.kill(MEDIUM));
        assertTrue(mgr.kill(MEDIUM).isEmpty());
        mgr.add(new Process(1001, LOW));
        mgr.add(new Process(1002, LOW));
        mgr.add(new Process(1004, MEDIUM));
        assertTrue(mgr.kill(HIGH).isEmpty());
        assertEquals(List.of(1001, 1002), mgr.kill(LOW));
        mgr.add(new Process(1005, HIGH));
        mgr.add(new Process(1006, HIGH));
        assertEquals(List.of(1004), mgr.kill(MEDIUM));

        assertThrows(IllegalArgumentException.class, () -> mgr.kill(null));
    }

    @Test
    public void killAll() {
        TaskManager mgr = new TaskManagerByPriority(10);
        IntStream.range(0, 10).forEach(i -> mgr.add(new Process(i, MEDIUM)));
        var killed = mgr.killAll();
        assertEquals(10, killed.size());
        assertTrue(mgr.listByInsertionTime().isEmpty());
    }
}
