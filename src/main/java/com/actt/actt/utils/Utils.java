package com.actt.actt.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
}
