package com.nodo.p2pnodo.service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nodo.p2pnodo.client.NodeClient;
import com.nodo.p2pnodo.model.FragmentInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final NodeClient nodeClient;
    private final FragmentService fragmentService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            FragmentInfo info = objectMapper.readValue(json, FragmentInfo.class);

            if (info.getFragmentId() == null || info.getNodeUrl() == null) {
                log.warn("⚠️ FragmentInfo incompleto: {}", info);
                return;
            }

            log.info("📥 Fragment event received: {}", info);

            if (!fragmentService.hasFragment(info.getFragmentId())) {
                log.info("🚀 Downloading fragment {} from node {}", info.getFragmentId(), info.getNodeUrl());
                nodeClient.downloadFragment(info.getNodeUrl(), info.getFragmentId());
            } else {
                log.info("✅ Already have fragment {}", info.getFragmentId());
            }
        } catch (Exception e) {
            log.error("❌ Error processing message from Redis", e);
        }
    }
}
