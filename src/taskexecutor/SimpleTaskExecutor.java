package taskexecutor;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleTaskExecutor implements TaskExecutor{
    private final ExecutorService executorService;
    private final ConcurrentHashMap<UUID, ReentrantLock> groupToLockMap;

    public SimpleTaskExecutor(int concurrency) {
        this.executorService = Executors.newFixedThreadPool(concurrency);
        this.groupToLockMap = new ConcurrentHashMap<>();
    }

    @Override
    public <T> Future<T> submitTask(Task<T> task) {
        ReentrantLock groupLock = groupToLockMap.computeIfAbsent(task.taskGroup().groupUUID(), k -> new ReentrantLock());
        return executorService.submit(() ->
        {
            groupLock.lock();
            try {
                return task.taskAction().call();
            }
            finally {
                groupLock.unlock();
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}