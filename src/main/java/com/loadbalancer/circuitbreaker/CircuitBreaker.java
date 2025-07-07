package com.loadbalancer.circuitbreaker;

import com.loadbalancer.enums.CircuitState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CircuitBreaker {

    private CircuitState state = CircuitState.CLOSED;
    private int failureCount = 0;
    private long lastFailureTime = 0;
    private boolean halfOpenTestInProgress = false;

    public synchronized boolean allowRequest() {
        if(state.equals(CircuitState.OPEN)) {
            long openTimeoutMillis = 10000;
            if(System.currentTimeMillis() - lastFailureTime > openTimeoutMillis){
                log.info("Circuit breaker transitioning to HALF_OPEN state");
                state = CircuitState.HALF_OPEN;
                halfOpenTestInProgress = true; // Indicate that we are testing the circuit
                failureCount = 0; // Reset failure count for half-open state
            } else {
                log.warn("Circuit breaker is OPEN, request denied");
                return false; // Circuit is open, deny request
            }
        }
        else if(state.equals(CircuitState.HALF_OPEN)){
            return !halfOpenTestInProgress;
        }
        return true;
    }

    public synchronized void recordFailure() {
        if (state == CircuitState.HALF_OPEN) {
            state = CircuitState.OPEN;
            lastFailureTime = System.currentTimeMillis();
            halfOpenTestInProgress = false;
        } else {
            failureCount++;
            int failureThreshold = 3;
            if (failureCount >= failureThreshold) {
                state = CircuitState.OPEN;
                lastFailureTime = System.currentTimeMillis();
            }
        }
    }

    public synchronized void recordSuccess() {
        state = CircuitState.CLOSED;
        failureCount = 0;
        halfOpenTestInProgress = false;
    }
}
