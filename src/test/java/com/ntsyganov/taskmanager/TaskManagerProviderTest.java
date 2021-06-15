package com.ntsyganov.taskmanager;

import org.junit.jupiter.api.Test;

import static com.ntsyganov.taskmanager.TaskManagerProvider.TaskManagerType.*;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerProviderTest {

    @Test
    public void getTaskManager() {
        TaskManager tm1 = TaskManagerProvider.getTaskManager();
        assertEquals(TaskManagerRejecting.class, tm1.getClass());
        TaskManager tm2 = TaskManagerProvider.getTaskManager(Rejecting);
        assertEquals(tm1, tm2);

        assertEquals(TaskManagerFifo.class, TaskManagerProvider.getTaskManager(Fifo).getClass());
        assertEquals(TaskManagerByPriority.class, TaskManagerProvider.getTaskManager(Priority).getClass());
    }

}