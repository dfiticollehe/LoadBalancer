package com.loadbalancer.service.impl;

import com.loadbalancer.core.LoadBalancer;
import com.loadbalancer.dto.resquest.ServerRegistrationRequestDto;
import com.loadbalancer.model.BackendServer;
import com.loadbalancer.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class AdminServerImpl implements AdminService {

    private final LoadBalancer loadBalancer;

    @Override
    public String registerServer(ServerRegistrationRequestDto serverRegistrationRequestDto) throws Exception {

        try {
            BackendServer backendServer = BackendServer.builder()
                    .host(serverRegistrationRequestDto.getHost())
                    .port(serverRegistrationRequestDto.getPort())
                    .weight(serverRegistrationRequestDto.getWeight())
                    .healthy(true)
                    .build();

            loadBalancer.registerServer(backendServer);
            return "Server registered successfully: " + backendServer.getHost() + ":" + backendServer.getPort();
        } catch (Exception e) {
            log.error("Error registering server: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String deregisterServer(ServerRegistrationRequestDto serverRegistrationRequestDto) throws Exception {
        try {
            BackendServer backendServer = BackendServer.builder()
                    .host(serverRegistrationRequestDto.getHost())
                    .port(serverRegistrationRequestDto.getPort())
                    .weight(serverRegistrationRequestDto.getWeight())
                    .healthy(false)
                    .build();

            loadBalancer.deregisterServer(backendServer);
            return "Server deregistered successfully: " + backendServer.getHost() + ":" + backendServer.getPort();
        } catch (Exception e) {
            log.error("Error deregistering server: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Map<String, BackendServer> getCircuitStates() throws Exception{
        try {
            List<BackendServer> servers = loadBalancer.getAllServers();

            return servers.stream()
                    .collect(Collectors.toMap(
                            BackendServer::getHost,     // assuming getId() returns a unique String
                            server -> server
                    ));
        } catch (Exception e) {
            log.error("Error retrieving circuit states: {}", e.getMessage(), e);
            throw e;
        }
    }

}
