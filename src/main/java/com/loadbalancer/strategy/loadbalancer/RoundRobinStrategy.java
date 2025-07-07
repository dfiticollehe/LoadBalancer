package com.loadbalancer.strategy.loadbalancer;

import com.loadbalancer.model.BackendServer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinStrategy implements LoadBalancingStrategy {

    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public BackendServer selectServer(List<BackendServer> servers) {

        int currentIndex = index.getAndUpdate(i -> (i + 1) % servers.size());
        return servers.get(currentIndex);

    }
}
