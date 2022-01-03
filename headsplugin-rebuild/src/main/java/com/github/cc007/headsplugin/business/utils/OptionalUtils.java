package com.github.cc007.headsplugin.business.utils;

import lombok.NonNull;

import java.util.Optional;
import java.util.function.Consumer;

public class OptionalUtils {

    @NonNull
    public static <T> Optional<T> peek(@NonNull Optional<T> self,
                                @NonNull Consumer<T> consumer) {
        return self.map(t -> {
            consumer.accept(t);
            return t;
        });
    }
}