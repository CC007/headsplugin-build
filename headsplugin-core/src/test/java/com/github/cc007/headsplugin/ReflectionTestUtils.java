package com.github.cc007.headsplugin;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class ReflectionTestUtils {

    @SneakyThrows
    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static <T, U> U getDeclaredFieldValue(Class<T> clazz, String fieldName, T obj) {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (U) field.get(obj);
    }

    @SneakyThrows
    public static <T> void setDeclaredFieldValue(Class<T> clazz, String fieldName, T obj,  Object value) {
        Field field = getDeclaredField(clazz, fieldName);
        field.set(obj, value);
    }
}