package com.example.Kanban.Board.utilities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ReflectionUtils {

    public static Field getFieldByAnnotation(
            Object object,
            Class<? extends Annotation> annotationClass) {

        Class<?> type = object.getClass();

        while (type != null) {

            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotationClass)) {
                    return field;
                }
            }

            type = type.getSuperclass();
        }

        return null;
    }

    public static Object getValueByAnnotation(
            Object object,
            Class<? extends Annotation> annotationClass)
            throws IllegalAccessException {

        Field field = getFieldByAnnotation(object, annotationClass);

        if (field == null) {
            return null;
        }

        field.setAccessible(true);

        return field.get(object);
    }

    public static void setValueByAnnotation(
            Object object,
            Class<? extends Annotation> annotationClass,
            Object value) throws IllegalAccessException {

        Field field = ReflectionUtils.getFieldByAnnotation(object, annotationClass);
        if (field == null) {
            throw new IllegalArgumentException(
                    "No field found with annotation: " + annotationClass.getName());
        }
        field.setAccessible(true);
        field.set(object, value);
        
    }
}
