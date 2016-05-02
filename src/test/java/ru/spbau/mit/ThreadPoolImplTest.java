package ru.spbau.mit;

import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Created by golovanov on 30.04.16.
 */

public final class ThreadPoolImplTest {
    private static final int N_THREADS = 10;
    private static final int N_TASKS = 1000;
    private static final ThreadPool T_POOL = new ThreadPoolImpl(N_THREADS);

    @Test
    public void submit() throws Exception {

        ArrayList<LightFuture<Integer>> tasks = new ArrayList<>();
        ArrayList<LightFuture<Integer>> tasksThenApply = new ArrayList<>();

        for (int i = 0; i < N_TASKS; i++) {
            Integer finalI = i;
            LightFuture<Integer> task = T_POOL.submit(() -> finalI);
            LightFuture<Integer> newTask = task.thenApply((a) -> a + 1);
            tasks.add(task);
            tasksThenApply.add(newTask);
        }

        for (int i = 0; i < N_TASKS; i++) {
            assertEquals(new Integer(i), tasks.get(i).get());
            assertEquals(new Integer(i + 1), tasksThenApply.get(i).get());
        }
    }

    @Test
    public void shutdown() throws Exception {

        LightFuture<Integer> task1 = T_POOL.submit(() -> 1);
        T_POOL.shutdown();
        LightFuture<Integer> task2 = T_POOL.submit(() -> 2);

        assertTrue(task1.isReady());
        assertFalse(task2.isReady());
    }

}
