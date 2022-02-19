package com.github.cc007.headsplugin.business.utils;

import lombok.val;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CollectionUtils {
    public static <T> Collection<List<T>> partitionCollection(Collection<T> collection, int chunkSize) {
        val counter = new AtomicInteger();
        return collection.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize))
                .values();
    }
}
