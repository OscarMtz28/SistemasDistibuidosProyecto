package com.nodo.p2pnodo.dto;



import java.util.List;

import lombok.Data;

@Data
public class NodeRegistration {
    private String nodeId;
    private String nodeUrl; // Ej: "http://nodo1:8080"
    private List<String> fragments; // Lista de fragmentos disponibles
}