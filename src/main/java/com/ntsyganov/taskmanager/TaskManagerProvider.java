package com.ntsyganov.taskmanager;

import java.util.Map;

import static com.ntsyganov.taskmanager.TaskManagerProvider.TaskManagerType.*;

public class TaskManagerProvider {

    private static final int CAPACITY = 10;

    public enum TaskManagerType {
        Rejecting, Fifo, Priority;

    }

    private static final Map<TaskManagerType, TaskManager> TASK_MANAGERS = Map.of(
            Rejecting, new TaskManagerRejecting(CAPACITY),
            Fifo, new TaskManagerFifo(CAPACITY),
            Priority, new TaskManagerByPriority(CAPACITY));

    public static TaskManager getTaskManager() {
        return getTaskManager(Rejecting);
    }

    public static TaskManager getTaskManager(TaskManagerType taskManagerType) {
        return TASK_MANAGERS.get(taskManagerType);
    }

}
