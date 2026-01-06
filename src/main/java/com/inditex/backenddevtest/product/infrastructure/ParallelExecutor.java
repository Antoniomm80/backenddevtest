package com.inditex.backenddevtest.product.infrastructure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

@Component
class ParallelExecutor {
    private final ExecutorService virtualThreadExecutor;

    ParallelExecutor(@Qualifier("virtualThreadExecutor") ExecutorService virtualThreadExecutor) {
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    <T, R> List<R> executeInParallel(List<T> items, Function<T, Optional<R>> mapper) {
        List<CompletableFuture<Optional<R>>> futures = items.stream()
                .map(item -> CompletableFuture.supplyAsync(() -> mapper.apply(item), virtualThreadExecutor))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
