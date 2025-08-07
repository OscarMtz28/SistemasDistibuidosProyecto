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

  public void onMessage(Message message, byte[] pattern) {
    try {
        String json = new String(message.getBody());
        FragmentInfo info = objectMapper.readValue(json, FragmentInfo.class);
        
        if (!info.isValid()) {
            log.warn("Evento inválido recibido: {}", json);
            return;
        }
        
        if (fragmentService.hasFragment(info.getFragmentId())) {
            log.debug("Fragmento {} ya existe localmente", info.getFragmentId());
            return;
        }
        
        log.info("Descargando fragmento {} de {}", info.getFragmentId(), info.getNodeUrl());
        nodeClient.downloadFragment(info.getNodeUrl(), info.getFragmentId());
        
    } catch (Exception e) {
        log.error("Error procesando mensaje Redis", e);
    }
}
}
