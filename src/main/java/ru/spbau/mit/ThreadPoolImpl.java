package ru.spbau.mit;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {

    private final LinkedList<Thread> threads;
    private LinkedList<LightFutureImpl> tasks;
    private volatile int sizeTasksList;
    private boolean isOn;

    private class LightFutureImpl<R> implements LightFuture<R> {

        private R result;
        private Throwable throwable;
        private volatile boolean isReady;
        private final Runnable evaluator;

        LightFutureImpl(final Supplier<R> supplier) {
            result = null;
            isReady = false;
            throwable = null;

            evaluator = () -> {
                try {
                    result = supplier.get();
                } catch (RuntimeException e) {
                    throwable = e;
                }

                isReady = true;
            };
        }

        <T> LightFutureImpl(final LightFutureImpl<? extends T> first,
                                    final Function<? super T, ? extends R> last) {
            result = null;
            isReady = false;
            throwable = null;

            evaluator = () -> {
                try {
                    T firstRes = first.get();
                    result = last.apply(firstRes);
                } catch (RuntimeException | LightExecutionException | InterruptedException e) {
                    throwable = e;
                }

                isReady = true;
            };
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
            LightFutureImpl<U> newTask = new LightFutureImpl<>(this, f);

            if (isOn) {
                addTask(newTask);
            }

            return newTask;
        }
    }

    public ThreadPoolImpl(int n) {
        threads = new LinkedList<>();
        tasks = new LinkedList<>();

        Runnable executor = () -> {
            try {
                while (!Thread.interrupted()) {
                    LightFutureImpl task = getTask();

                    synchronized (task) {
                        task.evaluator.run();
                        task.notifyAll();
                    }
                }
            } catch (InterruptedException e) { }
        };

        for (int i = 0; i < n; i++) {
            Thread newThread = new Thread(executor);
            threads.addLast(newThread);
            newThread.start();
        }

        isOn = true;
    }

    private synchronized <R> void addTask(LightFutureImpl<R> task) {
        tasks.addLast(task);
        sizeTasksList++;
        notify();
    }

    private synchronized <R> LightFutureImpl<R> getTask() throws InterruptedException {
        while (sizeTasksList == 0) {
            wait();
        }

        LightFutureImpl<R> task = tasks.pop();
        sizeTasksList--;

        if (sizeTasksList == 0 && !isOn) {
            notifyAll();
        }

        return task;
    }

    @Override
    public <R> LightFuture<R> submit(Supplier<R> supplier) {
        LightFutureImpl<R> newTask = new LightFutureImpl<>(supplier);
        if (isOn) {
            addTask(newTask);
        }

        return newTask;
    }

    @Override
    public synchronized void shutdown() {
        isOn = false;

        while (sizeTasksList != 0) {
            try {
                wait();
            } catch (InterruptedException e) { }
        }

        threads.forEach(Thread::interrupt);
    }
}
