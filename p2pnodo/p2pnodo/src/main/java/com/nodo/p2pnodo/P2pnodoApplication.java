package com.nodo.p2pnodo;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.nodo.p2pnodo.dto.NodeRegistration;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class P2pnodoApplication {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String centralServiceUrl = System.getenv("CENTRAL_SERVICE_URL");
    private final String serverPort = System.getenv("SERVER_PORT");
    private final String hostName = System.getenv("HOSTNAME");
    private final String nodeUrl = "http://" + hostName + ":" + serverPort;

    public static void main(String[] args) {
        SpringApplication.run(P2pnodoApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @PostConstruct
    public void registerOnStartup() {
        NodeRegistration registration = new NodeRegistration();
        registration.setNodeId(hostName);
        registration.setNodeUrl(nodeUrl);
        registration.setFragments(List.of());

        try {
            restTemplate.postForObject(
                centralServiceUrl + "/api/register",
                registration,
                String.class
            );
            System.out.println("✅ Nodo registrado en el servicio central con URL: " + nodeUrl);
        } catch (Exception e) {
            System.err.println("❌ Error registrando nodo: " + e.getMessage());
        }
    }
}
