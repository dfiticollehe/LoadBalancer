package com.loadbalancer.strategy.ratelimiting;

import com.loadbalancer.model.BackendServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class FixedWindowRateLimiter implements RateLimitingStrategy {

    private final int maxRequests;
    private final long windowSizeMillis;

    private static class Window {
        long startTime;
        AtomicInteger count;

        Window(long startTime) {
            this.startTime = startTime;
            this.count = new AtomicInteger(0);
        }
    }

    private final ConcurrentHashMap<BackendServer, Window> windowMap = new ConcurrentHashMap<>();


    @Override
    public boolean allowRequest(BackendServer backendServer){

        long now = System.currentTimeMillis();
        windowMap.putIfAbsent(backendServer, new Window(now));

        Window window = windowMap.get(backendServer);

        synchronized (window) {
            if (now - window.startTime > windowSizeMillis) {
                // New window
                window.startTime = now;
                window.count.set(0);
            }

            return window.count.incrementAndGet() <= maxRequests;
        }
    }
}
