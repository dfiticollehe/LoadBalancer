package com.loadbalancer.strategy.loadbalancer;

import com.loadbalancer.model.BackendServer;

import java.util.List;

public interface LoadBalancingStrategy {
    /**
     * Selects a backend server from the provided list based on the load balancing strategy.
     *
     * @param servers List of available backend servers.
     * @return Selected backend server.
     */
    BackendServer selectServer(List<BackendServer> servers);
}
