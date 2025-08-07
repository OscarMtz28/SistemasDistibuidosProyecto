package com.nodo.p2pnodo.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class FragmentService {

    private final String fragmentFolder = "fragments/";

    public FragmentService() {
        // Crear la carpeta fragments si no existe
        File dir = new File(fragmentFolder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Guarda un fragmento en la carpeta local
    public void saveFragment(String id, byte[] data) throws IOException {
        File file = new File(fragmentFolder + id + ".bin");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
    }

    // Devuelve el fragmento como bytes
    public byte[] getFragment(String id) throws IOException {
        File file = new File(fragmentFolder + id + ".bin");
        if (!file.exists()) {
            throw new IOException("Fragmento no encontrado");
        }
        return Files.readAllBytes(file.toPath());
    }

    // Verifica si el fragmento ya existe localmente
    public boolean hasFragment(String id) {
        File file = new File(fragmentFolder + id + ".bin");
        return file.exists();
    }
}
