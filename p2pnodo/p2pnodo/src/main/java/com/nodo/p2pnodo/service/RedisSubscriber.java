package com.nodo.p2pnodo.service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nodo.p2pnodo.client.NodeClient;
import com.nodo.p2pnodo.config.RedisConfig;
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
        final String channel = new String(pattern);
        
        if (!RedisConfig.FRAGMENT_CHANNEL.equals(channel)) {
            log.warn("Mensaje recibido en canal no esperado: {}", channel);
            return;
        }

        try {
            String json = new String(message.getBody());
            FragmentInfo info = objectMapper.readValue(json, FragmentInfo.class);
            
            if (!info.isValid()) {
                log.warn("Evento inválido recibido. Canal: {}, Contenido: {}", channel, json);
                return;
            }
            
            processFragment(info);
            
        } catch (Exception e) {
            log.error("Error procesando mensaje Redis. Canal: {}", channel, e);
            // Opcional: Métrica/alertas para monitoreo
        }
    }

    private void processFragment(FragmentInfo info) {
        final String fragmentId = info.getFragmentId();
        
        if (fragmentService.hasFragment(fragmentId)) {
            log.debug("Fragmento {} ya existe localmente. Saltando descarga.", fragmentId);
            return;
        }
        
        log.info("Iniciando descarga de fragmento {} desde {}", fragmentId, info.getNodeUrl());
        try {
            nodeClient.downloadFragment(info.getNodeUrl(), fragmentId);
            log.info("Descarga completada para fragmento {}", fragmentId);
        } catch (Exception e) {
            log.error("Falló la descarga del fragmento {}", fragmentId, e);
            // Opcional: Reintentar o notificar al central service
        }
    }
}