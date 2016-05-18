package ru.spbau.mit;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {

    private final List<Thread> threads = new LinkedList<>();
    private Queue<LightFutureImpl> tasks = new LinkedList<>();
    private boolean isOn;

    private class LightFutureImpl<R> implements LightFuture<R> {

        private R result = null;
        private Throwable throwable = null;
        private volatile boolean isReady = false;
        private volatile Queue<LightFutureImpl> thenApplyTasks = new LinkedList<>();
        private final Runnable evaluator;

        LightFutureImpl(final Supplier<? extends R> supplier) {
            evaluator = () -> {
                try {
                    result = supplier.get();
                } catch (Exception e) {
                    throwable = e;
                }

                isReady = true;
                submitThenApplyTasks();
            };
        }

        <T> LightFutureImpl(final LightFutureImpl<T> first,
                                    final Function<? super T, ? extends R> last) {
            evaluator = () -> {
                try {
                    T firstRes = first.get();
                    result = last.apply(firstRes);
                } catch (Exception e) {
                    throwable = e;
                }

                isReady = true;
                submitThenApplyTasks();
            };
        }

        private synchronized void submitThenApplyTasks() {
            thenApplyTasks.forEach(ThreadPoolImpl.this::addTask);
            thenApplyTasks.clear();
        }

        @Override
        public boolean isReady() {
            return isReady;
        }

        @Override
        public synchronized R get() throws LightExecutionException, InterruptedException {
            while (!isReady) {
                wait();
            }

            if (throwable != null) {
                throw new LightExecutionException(throwable);
            }
            return result;
        }

        @Override
        public <U> LightFuture<U> thenApply(Function<? super R, ? extends U> f) {
            if (!isOn) {
                return null;
            }

            LightFutureImpl<U> newTask = new LightFutureImpl<>(this, f);

            if (isReady) {
                addTask(newTask);
            } else {
                synchronized (this) {
                    if (isReady) {
                        addTask(newTask);
                    } else {
                        thenApplyTasks.add(newTask);
                    }
                }
            }

            return newTask;
        }
    }

    public ThreadPoolImpl(int n) {
        Runnable executor = () -> {
            try {
                while (!Thread.interrupted()) {
                    LightFutureImpl task = getTask();

                    task.evaluator.run();
                    synchronized (task) {
                        task.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                return; // stop thread
            }
        };

        for (int i = 0; i < n; i++) {
            Thread newThread = new Thread(executor);
            threads.add(newThread);
            newThread.start();
        }

        isOn = true;
    }

    private synchronized <R> void addTask(LightFutureImpl<R> task) {
        tasks.offer(task);
        notify();
    }

    private synchronized <R> LightFutureImpl<R> getTask() throws InterruptedException {
        while (tasks.size() == 0) {
            wait();
        }

        LightFutureImpl<R> task = tasks.peek();
        tasks.poll();

        if (tasks.size() == 0 && !isOn) {
            notifyAll();
        }

        return task;
    }

    @Override
    public <R> LightFuture<R> submit(Supplier<R> supplier) {
        if (isOn) {
            LightFutureImpl<R> newTask = new LightFutureImpl<>(supplier);
            addTask(newTask);
            return newTask;
        }

        return null;
    }

    @Override
    public synchronized void shutdown() {
        isOn = false;

        while (tasks.size() != 0) {
            try {
                wait();
            } catch (InterruptedException e) { }
        }

        threads.forEach(Thread::interrupt);
    }
}
