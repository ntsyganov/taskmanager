package com.ntsyganov.taskmanager;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ntsyganov.taskmanager.ProcessPriority.*;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerRejectingTest {

    private final Process process1Med = new Process(1001, MEDIUM);
    private final Process process2Low = new Process(1002, LOW);
    private final Process process3Low = new Process(1003, LOW);
    private final Process process4High = new Process(1004, HIGH);
    private final Process process5Med = new Process(1005, MEDIUM);
    private final Process process10Med = new Process(1010, MEDIUM);

    private final TaskManager mgr = new TaskManagerRejecting(3);

    @Test
    public void add() {
        assertTrue(mgr.add(new Process(1000, MEDIUM)));
        assertTrue(mgr.add(new Process(1001, MEDIUM)));
        assertTrue(mgr.add(new Process(1002, LOW)));
        assertFalse(mgr.add(new Process(1003, LOW)));
        assertFalse(mgr.add(new Process(1004, MEDIUM)));
        mgr.kill(1001);
        assertTrue(mgr.add(new Process(1004, MEDIUM)));
    }

    @Test
    public void listByInsertionTime() {
        assertTrue(mgr.listByInsertionTime().isEmpty());
        mgr.add(process1Med);
        mgr.add(process2Low);
        mgr.add(process3Low);
        mgr.add(process4High);
        assertEquals(List.of(process1Med, process2Low, process3Low), mgr.listByInsertionTime());
        mgr.kill(process1Med.getPid());
        mgr.add(process5Med);
        assertEquals(List.of(process2Low, process3Low, process5Med), mgr.listByInsertionTime());
    }

    @Test
    public void listByPriority() {
        assertTrue(new TaskManagerByPriority(3).listByPriority().isEmpty());
        mgr.add(process1Med);
        mgr.add(process2Low);
        mgr.add(process3Low);
        mgr.add(process4High);
        assertEquals(List.of(process2Low, process3Low, process1Med), mgr.listByPriority());
        mgr.kill(process1Med.getPid());
        mgr.add(process5Med);
        assertEquals(List.of(process2Low, process3Low, process5Med), mgr.listByPriority());
    }

    @Test
    public void listByPid() {
        assertTrue(mgr.listByPid().isEmpty());
        mgr.add(process10Med);
        mgr.add(process2Low);
        mgr.add(process3Low);
        mgr.add(process4High);
        assertEquals(List.of(process2Low, process3Low, process10Med), mgr.listByPid());
        mgr.kill(process10Med.getPid());
        mgr.add(process5Med);
        assertEquals(List.of(process2Low, process3Low, process5Med), mgr.listByPid());
    }

}