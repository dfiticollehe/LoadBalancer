package com.loadbalancer.model;

import com.loadbalancer.circuitbreaker.CircuitBreaker;
import lombok.*;

import java.util.Objects;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BackendServer {

    private String host;

    private int port;

    private int weight;

    private volatile boolean healthy = true;

    @Builder.Default
    private CircuitBreaker circuitBreaker = new CircuitBreaker();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackendServer that = (BackendServer) o;
        return port == that.port && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
