package com.p2pStream.centralservice.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.p2pStream.centralservice.config.RedisConfig;
import com.p2pStream.centralservice.dto.FragmentEvent;
import com.p2pStream.centralservice.dto.NodeRegistration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RegistryService {

    private final Map<String, Map<String, Object>> nodeRegistry = new ConcurrentHashMap<>();
    private final Map<String, String> fragmentMap = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;

    public RegistryService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void registerNode(NodeRegistration info) {
        

        Map<String, Object> nodeData = new HashMap<>();
        nodeData.put("url", info.getNodeUrl());
        nodeData.put("fragments", info.getFragments());
        nodeData.put("lastSeen", System.currentTimeMillis());
        
        nodeRegistry.put(info.getNodeId(), nodeData);
        log.info("Nodo {} registrado con URL: {}. Fragmentos: {}", 
                info.getNodeId(), info.getNodeUrl(), info.getFragments().size());

        info.getFragments().forEach(fragment -> {
            fragmentMap.put(fragment, info.getNodeUrl());
            publishFragment(new FragmentEvent(fragment, info.getNodeUrl()));
        });
    }

    public void publishFragment(FragmentEvent event) {
        if (event == null || !event.isValid()) {
            log.error("Intento de publicar evento inválido: {}", event);
            throw new IllegalArgumentException("FragmentEvent no puede ser nulo y debe ser válido");
        }

        event.setTimestamp(System.currentTimeMillis());
        
        try {
            redisTemplate.convertAndSend(RedisConfig.FRAGMENT_CHANNEL, event);
            fragmentMap.put(event.getFragmentId(), event.getNodeUrl());
            log.debug("Fragmento {} publicado correctamente en canal {} desde {}", 
                    event.getFragmentId(), RedisConfig.FRAGMENT_CHANNEL, event.getNodeUrl());
        } catch (Exception e) {
            log.error("Error al publicar fragmento {} en Redis: {}", event.getFragmentId(), e.getMessage());
            throw new RuntimeException("Error de comunicación con Redis", e);
        }
    }

    public Map<String, Map<String, Object>> getAllNodes() {
        return Collections.unmodifiableMap(nodeRegistry);
    }

    public Optional<String> getFragmentLocation(String fragmentId) {
        if (fragmentId == null || fragmentId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(fragmentMap.get(fragmentId));
    }

    
}