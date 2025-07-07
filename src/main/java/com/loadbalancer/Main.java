package com.loadbalancer;

import com.loadbalancer.core.LoadBalancer;
import com.loadbalancer.model.BackendServer;
import com.loadbalancer.server.LBRequestServer;
import com.loadbalancer.service.HealthChecker;
import com.loadbalancer.service.HealthMonitor;
import com.loadbalancer.strategy.HealthAwareStrategy;
import com.loadbalancer.strategy.loadbalancer.LoadBalancingStrategy;
import com.loadbalancer.strategy.loadbalancer.WeightedRoundRobinStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.List;

@Slf4j
@SpringBootApplication
public class Main implements CommandLineRunner{

    private final LoadBalancer loadBalancer;
    private final HealthChecker healthChecker;

    public Main(LoadBalancer loadBalancer, HealthChecker healthChecker) {
        this.loadBalancer = loadBalancer;
        this.healthChecker = healthChecker;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args); // Spring Boot startup
    }

    @Override
    public void run(String... args) {
        new Thread(new HealthMonitor(loadBalancer, healthChecker), "HealthMonitor-Thread").start();
        try {
            LBRequestServer lbRequestServer = new LBRequestServer(8080, loadBalancer);
            lbRequestServer.start();
        } catch (IOException e) {
            log.error("Failed to start Load Balancer server", e);
        }
    }

    @Bean
    public LoadBalancer loadBalancer() {
        List<BackendServer> servers = List.of(
                new BackendServer("localhost", 20001, 2, true),
                new BackendServer("localhost", 20002, 3, true),
                new BackendServer("localhost", 20003, 5, true)
        );
        LoadBalancingStrategy strategy = new HealthAwareStrategy(new WeightedRoundRobinStrategy());
        return new LoadBalancer(servers, strategy);
    }

    @Bean
    public HealthChecker healthChecker() {
        return new HealthChecker();
    }
}
