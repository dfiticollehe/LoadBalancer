package com.loadbalancer.service;

import com.loadbalancer.dto.resquest.ServerRegistrationRequestDto;
import com.loadbalancer.model.BackendServer;

import java.util.Map;

public interface AdminService {

    String registerServer(ServerRegistrationRequestDto serverRegistrationRequestDto) throws Exception;

    String deregisterServer(ServerRegistrationRequestDto serverRegistrationRequestDto) throws Exception;

    Map<String, BackendServer> getCircuitStates() throws Exception;
}
