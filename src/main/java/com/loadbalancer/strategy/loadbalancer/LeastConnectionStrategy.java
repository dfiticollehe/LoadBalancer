package com.loadbalancer.strategy.loadbalancer;

import com.loadbalancer.model.BackendServer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class LeastConnectionStrategy implements LoadBalancingStrategy {

    private final Map<String, Integer> connectionCounts;

    public LeastConnectionStrategy(List<BackendServer> servers) {
        this.connectionCounts = servers.stream()
                .collect(Collectors.toMap(s -> s.getHost() + ":" + s.getPort(), s -> 0));
    }

    @Override
    public BackendServer selectServer(List<BackendServer> servers) {
        BackendServer leastConnectionServer = null;
        int minConnections = Integer.MAX_VALUE;

        for (BackendServer server : servers) {
            int connections = connectionCounts.getOrDefault(server.getHost(), 0);
            if (connections < minConnections) {
                minConnections = connections;
                leastConnectionServer = server;
            }
        }

        if (leastConnectionServer != null) {
            connectionCounts.put(leastConnectionServer.getHost(), minConnections + 1);
            log.info("Routing request to least connection server: {}:{}", leastConnectionServer.getHost(), leastConnectionServer.getPort());
        }

        return leastConnectionServer;
    }

    public void incrementConnection(BackendServer server) {
        String key = server.getHost() + ":" + server.getPort();
        connectionCounts.put(key, connectionCounts.getOrDefault(key, 0) + 1);
    }

    public void decrementConnection(BackendServer server) {
        String key = server.getHost() + ":" + server.getPort();
        connectionCounts.put(key, Math.max(0, connectionCounts.getOrDefault(key, 0) - 1));
    }
}
