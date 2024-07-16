import taskexecutor.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {
    public static Task<Integer> buildTask(TaskGroup group, TaskType type, String name, String groupName) {
        Callable<Integer> taskAction = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("Executing Task: " + name + " from Group: " + groupName);
                int sleepSeconds = 3000;
                Thread.sleep(sleepSeconds);
                System.out.println("Completed Task: " + name + " from Group: " + groupName);
                return sleepSeconds;
            }
        };
        return new Task<>(UUID.randomUUID(), group, type, taskAction);
    }
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        TaskExecutor taskExecutor = new SimpleTaskExecutor(3);

        TaskGroup taskGroup1 = new TaskGroup(UUID.randomUUID());
        TaskGroup taskGroup2 = new TaskGroup(UUID.randomUUID());
        TaskGroup taskGroup3 = new TaskGroup(UUID.randomUUID());
        TaskGroup taskGroup4 = new TaskGroup(UUID.randomUUID());
        TaskGroup taskGroup5 = new TaskGroup(UUID.randomUUID());

        List<Future<Integer>> futures = new ArrayList<>();
        futures.add(taskExecutor.submitTask(buildTask(taskGroup1, TaskType.WRITE, "task1", "group1")));
        futures.add(taskExecutor.submitTask(buildTask(taskGroup2, TaskType.READ, "task1", "group2")));
        futures.add(taskExecutor.submitTask(buildTask(taskGroup3, TaskType.WRITE, "task1", "group3")));
        futures.add(taskExecutor.submitTask(buildTask(taskGroup4, TaskType.WRITE, "task1", "group4")));
        futures.add(taskExecutor.submitTask(buildTask(taskGroup5, TaskType.WRITE, "task1", "group5")));

        futures.add(taskExecutor.submitTask(buildTask(taskGroup4, TaskType.READ, "task2", "group4")));
        futures.add(taskExecutor.submitTask(buildTask(taskGroup5, TaskType.WRITE, "task2", "group5")));
        futures.add(taskExecutor.submitTask(buildTask(taskGroup3, TaskType.READ, "task2", "group3")));

        futures.add(taskExecutor.submitTask(buildTask(taskGroup1, TaskType.WRITE, "task3", "group1")));
        futures.add(taskExecutor.submitTask(buildTask(taskGroup2, TaskType.WRITE, "task3", "group2")));

        futures.add(taskExecutor.submitTask(buildTask(taskGroup2, TaskType.READ, "task4", "group2")));

        // Wait for results
        for (Future<Integer> future : futures) {
            future.get();
        }

        ((SimpleTaskExecutor) taskExecutor).shutdown();

    }
}