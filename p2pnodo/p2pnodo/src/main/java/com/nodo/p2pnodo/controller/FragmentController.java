package com.nodo.p2pnodo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
import com.nodo.p2pnodo.service.RegistrationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/fragment")
@RequiredArgsConstructor
public class FragmentController {

    private final FragmentService fragmentService;
    private final RegistrationService registrationService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFragment(@PathVariable String id) {
        try {
            byte[] data = fragmentService.getFragment(id);
            if (data == null || data.length == 0) {
                log.warn("⚠️ Fragmento '{}' no encontrado", id);
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            
            // Determinar el tipo de contenido basado en la extensión
            if (id.toLowerCase().endsWith(".mp4")) {
                headers.setContentType(MediaType.valueOf("video/mp4"));
            } else {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            }
            
            // Usar el nombre original del archivo
            String filename = id.contains(".") ? id : id + ".bin";
            headers.setContentDisposition(
                ContentDisposition.attachment()
                    .filename(filename)
                    .build());

            log.info("📤 Enviando fragmento {} ({} bytes)", id, data.length);
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
            
            // Notificar al servicio central que tenemos este fragmento
            registrationService.notifyFragmentAvailable(id);
            
            return ResponseEntity.ok("Fragmento '" + id + "' recibido correctamente.");

        } catch (IOException e) {
            log.error("❌ Error al guardar fragmento '{}': {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Error al guardar el fragmento: " + e.getMessage());
        }
    }
    @PostMapping("/upload-local")
public ResponseEntity<String> uploadLocalFile(@RequestParam String filename) {
    try {
        File file = new File("/app/mis_archivos_nodo/" + filename);
        if (!file.exists()) {
            return ResponseEntity.badRequest().body("Archivo no encontrado: " + filename);
        }

        byte[] data = Files.readAllBytes(file.toPath());
        fragmentService.saveFragment(filename, data);
        
        // Notificar al servicio central que tenemos este fragmento
        registrationService.notifyFragmentAvailable(filename);

        log.info("Archivo local '{}' cargado en fragmentos", filename);
        return ResponseEntity.ok("Archivo " + filename + " cargado en el nodo.");
    } catch (IOException e) {
        log.error("Error al cargar archivo local '{}': {}", filename, e.getMessage());
        return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
    }
}




}