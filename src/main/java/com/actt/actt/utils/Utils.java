package com.actt.actt.utils;

import javafx.concurrent.Task;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Utils {
    public static <T> List<T> getFieldsOfType(Object instance, Class<T> type) {
        List<T> result = new ArrayList<>();
        Field[] fields = instance.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (type.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    result.add(type.cast(field.get(instance)));
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static String loadSVGFromFile(String path) {
        try (InputStream inputStream = Utils.class.getResourceAsStream(path)) {
            assert inputStream != null;
            try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                return scanner.useDelimiter("\\A").next();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SVG file", e);
        }
    }

    public static <R> Task<R> makeFunctionAsync(Callable<R> callable) {
        return new Task<R>() {
            @Override
            protected R call() throws Exception {
                return callable.call();
            }
        };
    }

    public static <T> List<Future<T>> waitAllTasks(List<Task<T>> tasks) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(tasks.size());

        List<Callable<T>> callables = tasks.stream().map(task -> (Callable<T>)() -> {
            task.run();
            return task.get();
        }).toList();

        var futures = executorService.invokeAll(callables);
        executorService.shutdown();
        return futures;
    }
}
