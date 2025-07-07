package com.loadbalancer.core;

import com.loadbalancer.model.BackendServer;
import com.loadbalancer.strategy.loadbalancer.LoadBalancingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RequiredArgsConstructor
public class LoadBalancer {

    private final List<BackendServer> backendServers = new CopyOnWriteArrayList<>();
    private final LoadBalancingStrategy strategy;

    public LoadBalancer(List<BackendServer> initialServers, LoadBalancingStrategy strategy) {
        this.strategy = strategy;
        this.backendServers.addAll(initialServers);
    }

    public BackendServer getNextServer() {
        return strategy.selectServer(backendServers);
    }

    public void registerServer(BackendServer server) {
        backendServers.add(server);
        log.info("Backend registered: {}:{}", server.getHost(), server.getPort());
    }

    public void deregisterServer(BackendServer server) {
        backendServers.remove(server);
        log.info("Backend deregistered: {}:{}", server.getHost(), server.getPort());
    }

    public List<BackendServer> getAllServers() {
        return Collections.unmodifiableList(backendServers);
    }
}
