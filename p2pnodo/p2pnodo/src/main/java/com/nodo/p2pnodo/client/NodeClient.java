package com.nodo.p2pnodo.client;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.nodo.p2pnodo.service.FragmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NodeClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final FragmentService fragmentService;

    public void downloadFragment(String nodeUrl, String fragmentId) {
        // Verificar si ya tenemos el fragmento
        if (fragmentService.hasFragment(fragmentId)) {
            log.debug("Fragment {} already exists locally, skipping download", fragmentId);
            return;
        }

        String url = nodeUrl + "/fragment/" + fragmentId;
        log.info("🔄 Attempting to download fragment {} from {}", fragmentId, url);

        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                byte[] fragmentData = response.getBody();
                log.info("📦 Fragment {} downloaded ({} bytes)", fragmentId, fragmentData.length);

                // Guardar directamente en el servicio local
                try {
                    fragmentService.saveFragment(fragmentId, fragmentData);
                    log.info("✅ Fragment {} saved locally", fragmentId);
                } catch (Exception e) {
                    log.error("❌ Error saving fragment {} locally", fragmentId, e);
                }
            } else {
                log.warn("⚠️ Failed to download fragment {} from {} - Status: {}", 
                    fragmentId, nodeUrl, response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("❌ Error downloading fragment {} from {}: {}", fragmentId, nodeUrl, e.getMessage());
        }
    }

    public void uploadFragment(String targetNodeUrl, String fragmentId, byte[] data) {
        String url = targetNodeUrl + "/fragment/receive";

        ByteArrayResource fileResource = new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                return fragmentId + ".bin";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("id", fragmentId);
        body.add("file", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            log.info("📤 Uploaded fragment {} to {}", fragmentId, targetNodeUrl);
        } catch (Exception e) {
            log.error("❌ Failed to upload fragment to {}", targetNodeUrl, e);
        }
    }
}
