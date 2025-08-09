package com.p2pStream.centralservice.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class VideoSplitter {

    private static final int FRAGMENT_SIZE_KB = 512; // 512KB por fragmento (más seguro)
    private static final String FRAGMENT_PREFIX = "video_part_";

    public List<String> splitVideo(File videoFile, String outputDir) throws IOException {
        List<String> fragments = new ArrayList<>();
        byte[] buffer = new byte[FRAGMENT_SIZE_KB * 1024];
        
        try (InputStream is = new FileInputStream(videoFile)) {
            int bytesRead;
            int partCounter = 0;
            
            while ((bytesRead = is.read(buffer)) > 0) {
                String fragmentName = FRAGMENT_PREFIX + partCounter + ".mp4";
                Path fragmentPath = Path.of(outputDir, fragmentName);
                
                // SOLUCIÓN DEFINITIVA - 3 opciones:

                // Opción 1 (Recomendada): Usar FileOutputStream directamente
                try (FileOutputStream fos = new FileOutputStream(fragmentPath.toFile())) {
                    fos.write(buffer, 0, bytesRead);
                }
                fragments.add(fragmentName);
                partCounter++;
            }
        }
        return fragments;
    }
}