package com.p2pStream.centralservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

        for (String fragment : info.getFragments()) {
            fragmentMap.put(fragment, info.getNodeUrl());
            FragmentEvent event = new FragmentEvent();
            event.setFragmentId(fragment);
            event.setNodeUrl(info.getNodeUrl());
            redisTemplate.convertAndSend(RedisConfig.FRAGMENT_CHANNEL, event);
        }
    }

   public void publishFragment(FragmentEvent event) {
    event.setTimestamp(System.currentTimeMillis());
    redisTemplate.convertAndSend(RedisConfig.FRAGMENT_CHANNEL, event);
    fragmentMap.put(event.getFragmentId(), event.getNodeUrl());

    // Actualizar la lista de fragmentos en nodeRegistry
    nodeRegistry.forEach((nodeId, nodeData) -> {
        String url = (String) nodeData.get("url");
        if (url.equals(event.getNodeUrl())) {
            @SuppressWarnings("unchecked")
            List<String> fragments = (List<String>) nodeData.get("fragments");
            if (fragments == null) {
                fragments = new ArrayList<>();
                nodeData.put("fragments", fragments);
            }
            if (!fragments.contains(event.getFragmentId())) {
                fragments.add(event.getFragmentId());
            }
        }
    });
}


    public Map<String, Map<String, Object>> getAllNodes() {
        return Collections.unmodifiableMap(nodeRegistry);
    }

    public Optional<String> getFragmentLocation(String fragmentId) {
        return Optional.ofNullable(fragmentMap.get(fragmentId));
    }
}