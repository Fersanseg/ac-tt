package com.actt.actt.utils;

import javafx.util.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static <T> List<Pair<String, T>> getFieldsOfType(Object instance, Class<T> type) {
        List<Pair<String, T>> result = new ArrayList<>();
        Field[] fields = instance.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (type.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    result.add(new Pair<>(field.getName(), type.cast(field.get(instance))));
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
