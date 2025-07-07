package com.loadbalancer.service;

import com.loadbalancer.model.BackendServer;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class  HealthChecker {

    public boolean isServerHealthy(BackendServer server) throws Exception{
        try {
            //log.info("Calling health check for {}:{}", server.getHost(), server.getPort());
            URL url = new URL("http://" + server.getHost() + ":" + server.getPort() + "/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);  // 1 sec timeout
            connection.setReadTimeout(1000);
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            log.error("Health check failed for {}:{}", server.getHost(), server.getPort());
            return false;
        }
    }
}
