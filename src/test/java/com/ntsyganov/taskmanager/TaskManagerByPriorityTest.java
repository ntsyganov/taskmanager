package com.ntsyganov.taskmanager;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ntsyganov.taskmanager.ProcessPriority.*;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerByPriorityTest {

    private final Process process1Med = new Process(1001, MEDIUM);
    private final Process process2Low = new Process(1002, LOW);
    private final Process process3Low = new Process(1003, LOW);
    private final Process process4High = new Process(1004, HIGH);
    private final Process process5Med = new Process(1005, MEDIUM);
    private final Process process10Med = new Process(1010, MEDIUM);

    private final TaskManager mgr = new TaskManagerByPriority(3);

    @Test
    public void add() {
        assertTrue(mgr.add(new Process(1000, MEDIUM)));
        assertTrue(mgr.add(new Process(1001, LOW)));
        assertTrue(mgr.add(new Process(1002, LOW)));
        assertFalse(mgr.add(new Process(1003, LOW)));
        assertTrue(mgr.add(new Process(1004, MEDIUM)));

        mgr.kill(1001);
        assertFalse(mgr.add(new Process(1005, LOW)));
        assertTrue(mgr.add(new Process(1005, MEDIUM)));
        assertTrue(mgr.add(new Process(1005, HIGH)));
    }

    @Test
    public void listByInsertionTime() {
        assertTrue(mgr.listByInsertionTime().isEmpty());
        mgr.add(process1Med);
        mgr.add(process2Low);
        mgr.add(process3Low);
        mgr.add(process4High);
        assertEquals(List.of(process1Med, process3Low, process4High), mgr.listByInsertionTime());
        mgr.kill(process1Med.getPid());
        mgr.add(process5Med);
        assertEquals(List.of(process3Low, process4High, process5Med), mgr.listByInsertionTime());
    }

    @Test
    public void listByPriority() {
        assertTrue(new TaskManagerByPriority(3).listByPriority().isEmpty());
        mgr.add(process1Med);
        mgr.add(process2Low);
        mgr.add(process3Low);
        mgr.add(process4High);
        assertEquals(List.of(process3Low, process1Med, process4High), mgr.listByPriority());
        mgr.kill(process1Med.getPid());
        mgr.add(process5Med);
        assertEquals(List.of(process3Low, process5Med, process4High), mgr.listByPriority());
    }

    @Test
    public void listByPid() {
        assertTrue(mgr.listByPid().isEmpty());
        mgr.add(process10Med);
        mgr.add(process2Low);
        mgr.add(process3Low);
        mgr.add(process4High);
        assertEquals(List.of(process3Low, process4High, process10Med), mgr.listByPid());
        mgr.kill(process10Med.getPid());
        mgr.add(process5Med);
        assertEquals(List.of(process3Low, process4High, process5Med), mgr.listByPid());
    }

}