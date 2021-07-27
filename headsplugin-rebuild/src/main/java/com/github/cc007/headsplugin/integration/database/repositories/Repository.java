package com.github.cc007.headsplugin.integration.database.repositories;

import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface Repository<T, ID>  {
    @SneakyThrows({IllegalAccessException.class, InstantiationException.class, InvocationTargetException.class, NoSuchMethodException.class, })
    T manageNew();

    T manage(T entity);

    List<T> findAll();
}
