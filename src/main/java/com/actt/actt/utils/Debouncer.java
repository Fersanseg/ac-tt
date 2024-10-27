package com.actt.actt.utils;

import javafx.concurrent.Task;

import java.util.concurrent.*;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class Debouncer<T> {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;

    public ScheduledFuture<?> runDelayed(Callable<T> callable, int milis) {
        cancel();

        scheduledFuture = scheduler.schedule(callable, milis, TimeUnit.MILLISECONDS);
        return scheduledFuture;
    }

    public void runDelayed(Callable<T> callable, int milis, Consumer<T> onSuccessCallback) {
        ScheduledFuture<T> future = (ScheduledFuture<T>) runDelayed(callable, milis);

        var task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return future.get();
            }

            @Override
            protected void succeeded() {
                onSuccessCallback.accept((T) getValue());
            }
        };
        new Thread(task).start();
    }

    public void runDelayed(Callable<T> callable, int milis, Consumer<T> onSuccessCallback, Runnable onFailureCallback) {
        ScheduledFuture<T> future = (ScheduledFuture<T>) runDelayed(callable, milis);

        var task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return future.get();
            }

            @Override
            protected void succeeded() {
                onSuccessCallback.accept((T) getValue());
            }

            @Override
            protected void failed() {
                onFailureCallback.run();
            }
        };
        new Thread(task).start();
    }

    public void cancel() {
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(false);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
