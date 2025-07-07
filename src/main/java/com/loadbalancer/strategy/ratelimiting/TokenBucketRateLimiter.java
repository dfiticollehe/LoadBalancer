package com.loadbalancer.strategy.ratelimiting;

import com.loadbalancer.model.BackendServer;

public class TokenBucketRateLimiter implements RateLimitingStrategy {

    @Override
    public boolean allowRequest(BackendServer backendServer){
        return true;
    }
}
