package com.p2pStream.centralservice.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.p2pStream.centralservice.dto.FragmentEvent;
import com.p2pStream.centralservice.service.RegistryService;
import com.p2pStream.centralservice.util.VideoSplitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoSplitter videoSplitter;
    private final RegistryService registryService;
    private final Path fragmentsDir = Paths.get("video_fragments");

    public VideoController(VideoSplitter videoSplitter, RegistryService registryService) {
        this.videoSplitter = videoSplitter;
        this.registryService = registryService;
        createFragmentsDirectory();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            // 1. Validar archivo
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo está vacío");
            }

            // 2. Guardar temporalmente
            File tempFile = File.createTempFile("upload_", ".tmp");
            file.transferTo(tempFile);

            // 3. Dividir video
            List<String> fragments = videoSplitter.splitVideo(tempFile, fragmentsDir.toString());
            tempFile.delete(); // Limpieza

            // 4. Distribuir fragmentos
            distributeFragments(fragments);

            return ResponseEntity.ok().body(
                String.format("Video dividido en %d fragmentos. Directorio: %s", 
                fragments.size(), fragmentsDir.toAbsolutePath())
            );

        } catch (Exception e) {
            log.error("Error al procesar video", e);
            return ResponseEntity.internalServerError()
                .body("Error: " + e.getMessage());
        }
    }

    private void createFragmentsDirectory() {
        try {
            if (!fragmentsDir.toFile().exists()) {
                fragmentsDir.toFile().mkdirs();
                log.info("Directorio de fragmentos creado: {}", fragmentsDir.toAbsolutePath());
            }
        } catch (Exception e) {
            log.error("No se pudo crear el directorio de fragmentos", e);
        }
    }

    private void distributeFragments(List<String> fragments) {
        fragments.forEach(fragment -> {
            try {
                FragmentEvent event = new FragmentEvent();
                event.setFragmentId(fragment);
                
                // Asignación inteligente a nodos disponibles
                String targetNode = selectOptimalNode(); // Implementa tu lógica aquí
                event.setNodeUrl(targetNode);
                
                registryService.publishFragment(event);
                log.info("Fragmento {} asignado a {}", fragment, targetNode);
                
            } catch (Exception e) {
                log.error("Error distribuyendo fragmento {}", fragment, e);
            }
        });
    }

    private String selectOptimalNode() {
        // Implementación básica (mejorable)
        return registryService.getAllNodes().values().stream()
            .findFirst()
            .map(node -> (String) node.get("url"))
            .orElse("http://nodo-default:8080");
    }
}