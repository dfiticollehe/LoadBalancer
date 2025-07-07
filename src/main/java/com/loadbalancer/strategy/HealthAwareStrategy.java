package com.loadbalancer.strategy;

import com.loadbalancer.model.BackendServer;
import com.loadbalancer.strategy.loadbalancer.LoadBalancingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class HealthAwareStrategy implements LoadBalancingStrategy {

    private final LoadBalancingStrategy delegate;

    @Override
    public BackendServer selectServer(List<BackendServer> allServers) {
        List<BackendServer> healthyServers = allServers.stream()
                .filter(BackendServer::isHealthy)
                .collect(Collectors.toList());

        if (healthyServers.isEmpty()) {
            throw new IllegalStateException("No healthy backend servers available");
        }

        return delegate.selectServer(healthyServers);
    }
}
