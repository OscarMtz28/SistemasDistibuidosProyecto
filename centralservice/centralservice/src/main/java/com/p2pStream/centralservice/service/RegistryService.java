package com.p2pStream.centralservice.service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.p2pStream.centralservice.dto.FragmentEvent;
import com.p2pStream.centralservice.dto.NodeInfo;

@Service
public class RegistryService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<String, List<String>> nodeRegistry = new ConcurrentHashMap<>();
    private final Map<String, String> fragmentMap = new ConcurrentHashMap<>();

    public RegistryService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void registerNode(NodeInfo info) {
        nodeRegistry.put(info.getNodeId(), info.getFragments());
        for (String fragment : info.getFragments()) {
            fragmentMap.put(fragment, info.getNodeId());

            // Publicar evento en Redis
            FragmentEvent event = new FragmentEvent();
            event.setFragmentId(fragment);
            event.setNodeId(info.getNodeId());

            redisTemplate.convertAndSend("fragment-available", event);
        }
    }

    public Map<String, List<String>> getAllNodes() {
        return nodeRegistry;
    }

    public Optional<String> getFragmentLocation(String fragmentId) {
        return Optional.ofNullable(fragmentMap.get(fragmentId));
    }
}
