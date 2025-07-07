package com.loadbalancer.strategy.ratelimiting;

import com.loadbalancer.model.BackendServer;

public interface RateLimitingStrategy {

    /**
     * Returns true if the request is allowed for the given backend server.
     *
     * @param backendServer the backend server to rate-limit
     * @return true if the request is allowed, false if it's rate-limited
     */
    boolean allowRequest(BackendServer backendServer);
}
