package com.loadbalancer.enums;

public enum CircuitState {

    CLOSED,     // Everything working
    OPEN,       // Failures detected, skip backend
    HALF_OPEN   // Try a few requests to test recovery
}
