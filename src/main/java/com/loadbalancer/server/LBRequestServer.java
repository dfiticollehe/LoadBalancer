package com.loadbalancer.server;

import com.loadbalancer.core.LoadBalancer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class LBRequestServer {

    private final int port;
    private final LoadBalancer loadBalancer;

    /**
     * Starts the LBRequestServer to listen for incoming client requests.
     * Each request is handled by a new Worker thread that forwards the request
     * to a backend server selected by the LoadBalancer.
     *
     * @throws IOException if an I/O error occurs when opening the socket
     */
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        log.info("Load Balancer started on port {}", port);

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                log.info("New client connected: {}", clientSocket.getInetAddress());

                // Dispatch to worker
                Worker worker = new Worker(clientSocket, loadBalancer);
                new Thread(worker).start();
            } catch (IOException e) {
                log.error("Error accepting client connection", e);
            }
        }
    }
}
