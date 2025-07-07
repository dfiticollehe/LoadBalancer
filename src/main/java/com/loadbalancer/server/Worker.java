package com.loadbalancer.server;

import com.loadbalancer.core.LoadBalancer;
import com.loadbalancer.model.BackendServer;
import com.loadbalancer.strategy.ratelimiting.RateLimitingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class Worker implements Runnable{

    private final Socket clientSocket;
    private final LoadBalancer loadBalancer;
    private final RateLimitingStrategy rateLimiter;

    @Override
    public void run() {
        handleClientRequest(clientSocket);
    }

    private void handleClientRequest(Socket clientSocket) {
        try (clientSocket; Socket backendSocket = connectWithRetry(3)) {
            try {

                if (backendSocket == null) {
                    log.error("Failed to connect to any backend. Request cannot be processed.");
                    return;
                }

                InputStream clientIn = clientSocket.getInputStream();
                OutputStream clientOut = clientSocket.getOutputStream();
                InputStream backendIn = backendSocket.getInputStream();
                OutputStream backendOut = backendSocket.getOutputStream();

                // Start forwarding threads
                Thread clientToBackend = new Thread(() -> forwardData(clientIn, backendOut));
                Thread backendToClient = new Thread(() -> forwardData(backendIn, clientOut));

                clientToBackend.start();
                backendToClient.start();

                clientToBackend.join();
                backendToClient.join();

            } catch (IOException | InterruptedException e) {
                log.error("Worker error", e);
            }
        } catch (IOException e) {
            log.warn("Failed to close client socket", e);
        }
    }

    private Socket connectToBackend() throws IOException {
        BackendServer backend = loadBalancer.getNextServer();
        log.info("Forwarding to backend {}:{}", backend.getHost(), backend.getPort());
        return new Socket(backend.getHost(), backend.getPort());
    }

    private void forwardData(InputStream in, OutputStream out) {
        try {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
                out.flush();
            }
        } catch (IOException e) {
            log.error("Stream forwarding interrupted: {}", e.getMessage());
        }
    }

    private Socket connectWithRetry(int maxRetries) throws IOException {
        int attempts = 0;
        Set<BackendServer> tried = new HashSet<>();
        while (attempts < maxRetries) {
            try {
                BackendServer backend = loadBalancer.getNextServer();
                if(tried.contains(backend)) {
                    log.warn("Already tried backend {}:{}. Skipping to next.", backend.getHost(), backend.getPort());
                    if (tried.size() >= loadBalancer.getAllServers().size()) {
                        throw new IOException("No more backends to try. All failed or repeated.");
                    }
                    continue;
                }
                /* Check rate limiter before trying to connect */
                if (!rateLimiter.allowRequest(backend)) {
                    log.warn("Rate limit exceeded for backend {}:{}. Skipping.", backend.getHost(), backend.getPort());
                    tried.add(backend);
                    continue;
                }
                tried.add(backend);
                log.info("Attempting to connect to backend {}:{}", backend.getHost(), backend.getPort());
                return new Socket(backend.getHost(), backend.getPort());
            } catch (IOException e) {
                attempts++;
                log.warn("Connection attempt {} failed, retrying...", attempts);
                if (attempts >= maxRetries) {
                    throw e; // Rethrow after max retries
                }
            }
        }
        return null;
    }
}
