package com.nodo.p2pnodo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nodo.p2pnodo.dto.NodeRegistration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RestTemplate restTemplate;
    
    @Value("${central.service.url:http://centralservice:8080}")
    private String centralServiceUrl;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${hostname:localhost}")
    private String hostName;

    public void notifyFragmentAvailable(String fragmentId) {
        try {
            String nodeUrl = "http://" + hostName + ":" + serverPort;
            
            NodeRegistration registration = new NodeRegistration();
            registration.setNodeId(hostName);
            registration.setNodeUrl(nodeUrl);
            registration.setFragments(List.of(fragmentId));

            restTemplate.postForObject(
                centralServiceUrl + "/api/register",
                registration,
                String.class
            );
            
            log.info("✅ Notificado al servicio central que tenemos el fragmento: {}", fragmentId);
        } catch (Exception e) {
            log.error("❌ Error notificando fragmento {} al servicio central: {}", fragmentId, e.getMessage());
        }
    }
}