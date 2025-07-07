package com.loadbalancer.controller;

import com.loadbalancer.dto.response.GeneralResponse;
import com.loadbalancer.dto.resquest.ServerRegistrationRequestDto;
import com.loadbalancer.model.BackendServer;
import com.loadbalancer.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/register")
    GeneralResponse<String, Void> registerServer(@RequestBody ServerRegistrationRequestDto serverRegistrationRequestDto) throws Exception {

        return GeneralResponse.<String, Void>builder()
                .statusCode(HttpStatus.OK.value())
                .data(adminService.registerServer(serverRegistrationRequestDto))
                .build();
    }

    @PutMapping("/deregister")
    GeneralResponse<String, Void> deregisterServer(@RequestBody ServerRegistrationRequestDto serverRegistrationRequestDto) throws Exception {

        return GeneralResponse.<String, Void>builder()
                .statusCode(HttpStatus.OK.value())
                .data(adminService.registerServer(serverRegistrationRequestDto))
                .build();
    }

    @GetMapping("/circuit-state")
    public GeneralResponse<Map<String, BackendServer>,Void> getCircuitStates() {

    }

}
