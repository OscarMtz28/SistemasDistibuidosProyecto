package com.p2pStream.centralservice.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    @GetMapping("/fragment/{fragmentId}")
    public ResponseEntity<byte[]> getFragment(@PathVariable String fragmentId) {
        try {
            File fragmentFile = new File(fragmentsDir.toFile(), fragmentId);
            
            if (!fragmentFile.exists()) {
                log.warn("Fragmento no encontrado: {}", fragmentId);
                return ResponseEntity.notFound().build();
            }

            byte[] data = Files.readAllBytes(fragmentFile.toPath());
            
            HttpHeaders headers = new HttpHeaders();
            if (fragmentId.toLowerCase().endsWith(".mp4")) {
                headers.setContentType(MediaType.valueOf("video/mp4"));
            } else {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            }
            
            headers.setContentDisposition(
                ContentDisposition.attachment()
                    .filename(fragmentId)
                    .build());

            log.info("Sirviendo fragmento {} ({} bytes)", fragmentId, data.length);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);

        } catch (IOException e) {
            log.error("Error al leer fragmento {}: {}", fragmentId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private String selectOptimalNode() {
        // Implementación básica (mejorable)
        return registryService.getAllNodes().values().stream()
            .findFirst()
            .map(node -> (String) node.get("url"))
            .orElse("http://nodo-default:8080");
    }
}