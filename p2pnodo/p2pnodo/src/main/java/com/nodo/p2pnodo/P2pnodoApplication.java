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
    private final String CENTRAL_SERVICE_URL = System.getenv("CENTRAL_SERVICE_URL");
    private final String NODE_PORT = System.getenv("SERVER_PORT");
    private final String NODE_URL = "http://" + System.getenv("HOSTNAME") + ":" + NODE_PORT;

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
        registration.setNodeId(System.getenv("HOSTNAME"));
        registration.setNodeUrl(NODE_URL);
        registration.setFragments(List.of());

        try {
            restTemplate.postForObject(
                CENTRAL_SERVICE_URL + "/api/register",
                registration,
                String.class
            );
            System.out.println("✅ Nodo registrado en el servicio central");
        } catch (Exception e) {
            System.err.println("❌ Error registrando nodo: " + e.getMessage());
        }
    }
}
