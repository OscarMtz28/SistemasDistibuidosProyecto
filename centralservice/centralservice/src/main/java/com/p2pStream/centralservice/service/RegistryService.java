package com.p2pStream.centralservice.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.p2pStream.centralservice.dto.FragmentEvent;
import com.p2pStream.centralservice.dto.NodeRegistration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RegistryService {

    private final Map<String, Map<String, Object>> nodeRegistry = new ConcurrentHashMap<>();
    private final Map<String, String> fragmentMap = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;

    // Inyección de dependencia a través del constructor
    public RegistryService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void registerNode(NodeRegistration info) {
        // 1. Registrar información completa del nodo
        Map<String, Object> nodeData = new HashMap<>();
        nodeData.put("url", info.getNodeUrl());
        nodeData.put("fragments", info.getFragments());
        nodeData.put("lastSeen", System.currentTimeMillis());
        
        nodeRegistry.put(info.getNodeId(), nodeData);

        // 2. Actualizar mapeo de fragmentos
        for (String fragment : info.getFragments()) {
            fragmentMap.put(fragment, info.getNodeUrl());
            
            // 3. Publicar evento
            FragmentEvent event = new FragmentEvent();
            event.setFragmentId(fragment);
            event.setNodeUrl(info.getNodeUrl());
            event.setTimestamp(System.currentTimeMillis());
            
            redisTemplate.convertAndSend("fragment-available", event);
        }
    }

    public void publishFragment(FragmentEvent event) {
    // Asegurar que el evento tenga timestamp
    if (event.getTimestamp() == null) {
        event.setTimestamp(System.currentTimeMillis());
    }
    
    // Publicar en Redis
    redisTemplate.convertAndSend("fragment-available", event);
    
    // Registrar en el fragmentMap (opcional)
    fragmentMap.put(event.getFragmentId(), event.getNodeUrl());
    
    log.info("Fragmento {} publicado desde {}", event.getFragmentId(), event.getNodeUrl());
       }

    public Map<String, Map<String, Object>> getAllNodes() {
        return Collections.unmodifiableMap(nodeRegistry);
    }

    public Optional<String> getFragmentLocation(String fragmentId) {
        return Optional.ofNullable(fragmentMap.get(fragmentId));
    }
}
