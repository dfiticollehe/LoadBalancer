package com.loadbalancer.service;

import com.loadbalancer.core.LoadBalancer;
import com.loadbalancer.model.BackendServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HealthMonitor implements Runnable {

    private final LoadBalancer loadBalancer;
    private final HealthChecker healthChecker;
    private volatile boolean running = true;

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            for (BackendServer backendServer : loadBalancer.getAllServers()) {
                boolean isHealthy;
                try {
                    isHealthy = healthChecker.isServerHealthy(backendServer);
                } catch (Exception e) {
                    isHealthy = false;
                    log.error("Health check failed for {}:{}", backendServer.getHost(), backendServer.getPort(), e);
                }

                boolean oldStatus = backendServer.isHealthy();
                backendServer.setHealthy(isHealthy);
                if (oldStatus != isHealthy) {
                    if (isHealthy) {
                        log.info("Backend server {}:{} is now healthy", backendServer.getHost(), backendServer.getPort());
                    } else {
                        log.warn("Backend server {}:{} is now unhealthy", backendServer.getHost(), backendServer.getPort());
                    }
                }
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.info("HealthMonitor stopped.");
    }
}

