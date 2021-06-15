# Overview

There is no main class as it's assumed to be the library code, used by the application/service.

[`TaskManagerProvider.java`](src/main/java/com/ntsyganov/taskmanager/TaskManagerProvider.java) can be considered as an
entry point, with the example of usage in the appropriate unit test. In the real life this class can be constructed (
e.g. initialiisation of the capacity and the task manager map) by a Dependency Injection framework configuration class,
based on the build time configuration or run time properties (like property files or system properties). This class can
provide [`TaskManager.java`](src/main/java/com/ntsyganov/taskmanager/TaskManager.java) in a multi-tenancy setup (e.g.
deployment in the cloud serves several clients with different requirements).

# Assumptions and design choices

The following requirements were assumed:

- No particular performance requirements for the operations (i.e. which operations should be optimized)
- The requirement to support several clients and implementations.

Therefore the implementation leaned towards code unification, re-usage and clarity as opposed to the effectiveness of
the particular operations.
Hence [`TaskManagerRejecting.java`](src/main/java/com/ntsyganov/taskmanager/TaskManagerRejecting.java)
and [`TaskManagerFifo.java`](src/main/java/com/ntsyganov/taskmanager/TaskManagerFifo.java) use the same queue, although
potentially [`TaskManagerRejecting.java`](src/main/java/com/ntsyganov/taskmanager/TaskManagerRejecting.java)
could use another data structure to improve performance of the killing by id operation. Also, the lock for the whole
operation is acquired, while in the real life potentially less strict guarantees could be applied in order to improve
performance in the multithreading environment (with the use of java.util.concurrent package's collections etc).

Another assumption is that the native OS process is created outside of this library and already bound to
the [`Process.java`](src/main/java/com/ntsyganov/taskmanager/Process.java) class. Some other assumptions are mentioned
in the code as comments.





