package com.nodo.p2pnodo.controller;

import java.io.IOException;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nodo.p2pnodo.service.FragmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/fragment")
@RequiredArgsConstructor
public class FragmentController {

    private final FragmentService fragmentService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFragment(@PathVariable String id) {
        try {
            byte[] data = fragmentService.getFragment(id);
            if (data == null || data.length == 0) {
                log.warn("⚠️ Fragmento '{}' no encontrado", id);
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(
                ContentDisposition.attachment()
                    .filename(id + ".bin")
                    .build());

            log.info("📤 Enviando fragmento {}", id);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);

        } catch (IOException e) {
            log.error("❌ Error al obtener fragmento '{}': {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/receive")
    public ResponseEntity<String> receiveFragment(
            @RequestParam("id") String id,
            @RequestParam("file") MultipartFile file) {
        
        try {
            if (file.isEmpty()) {
                log.warn("⚠️ Archivo vacío recibido para fragmento '{}'", id);
                return ResponseEntity.badRequest().body("Archivo vacío");
            }

            fragmentService.saveFragment(id, file.getBytes());
            log.info("✅ Fragmento '{}' recibido ({} bytes)", id, file.getSize());
            return ResponseEntity.ok("Fragmento '" + id + "' recibido correctamente.");

        } catch (IOException e) {
            log.error("❌ Error al guardar fragmento '{}': {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Error al guardar el fragmento: " + e.getMessage());
        }
    }
}