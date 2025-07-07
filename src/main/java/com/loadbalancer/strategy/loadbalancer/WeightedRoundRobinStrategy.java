package com.loadbalancer.strategy.loadbalancer;

import com.loadbalancer.model.BackendServer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class WeightedRoundRobinStrategy implements LoadBalancingStrategy {

    private int currentWeight = 0;

    @Override
    public BackendServer selectServer(List<BackendServer> servers) {

        int totalWeight = servers.stream().mapToInt(BackendServer::getWeight).sum();
        int weight = 0;

        for (BackendServer server : servers) {
            weight += server.getWeight();
            if (currentWeight < weight) {
                currentWeight = (currentWeight + 1) % totalWeight;
                log.info("Routing request to weighted server: {}:{}", server.getHost(), server.getPort());
                return server;
            }
        }
        log.warn("No server selected, this should not happen if weights are set correctly.");
        return null;
    }
}
