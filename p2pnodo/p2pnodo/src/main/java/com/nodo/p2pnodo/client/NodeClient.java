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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NodeClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public void downloadFragment(String nodeUrl, String fragmentId) {
        String url = nodeUrl + "/fragment/" + fragmentId;

        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

            if (response.getStatusCode().is2xxSuccessful()) {
                byte[] fragmentData = response.getBody();
                log.info("📦 Fragment {} downloaded ({} bytes)", fragmentId, fragmentData.length);

                // Enviarlo a mí mismo
                uploadFragment("http://localhost:8080", fragmentId, fragmentData);
            } else {
                log.warn("⚠️ Failed to download fragment {} from {}", fragmentId, nodeUrl);
            }

        } catch (Exception e) {
            log.error("❌ Error downloading fragment {}", fragmentId, e);
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
