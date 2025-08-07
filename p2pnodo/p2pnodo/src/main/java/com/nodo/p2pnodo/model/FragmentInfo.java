package com.nodo.p2pnodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FragmentInfo {

    private String fragmentId;
    private String nodeUrl;

    // Opcional: puedes agregar timestamp o nodo que originó el mensaje
    private Long timestamp; // epoch millis

    // Validación básica útil para logs/debug
    public boolean isValid() {
        return fragmentId != null && !fragmentId.isBlank()
            && nodeUrl != null && !nodeUrl.isBlank();
    }

    @Override
    public String toString() {
        return "FragmentInfo{" +
                "fragmentId='" + fragmentId + '\'' +
                ", nodeUrl='" + nodeUrl + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
