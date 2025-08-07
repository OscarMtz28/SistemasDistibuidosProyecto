package com.p2pStream.centralservice.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.p2pStream.centralservice.dto.NodeRegistration;
import com.p2pStream.centralservice.service.RegistryService;

@RestController
@RequestMapping("/api")
public class RegistryController {

    private final RegistryService registryService;  // Nombre más descriptivo

    public RegistryController(RegistryService registryService) {
        this.registryService = registryService;
    }

    @PostMapping("/register")
    public String registerNode(@RequestBody NodeRegistration info) {
        registryService.registerNode(info);
        return String.format(
            "Node %s registered with URL: %s. Fragments: %d",
            info.getNodeId(),
            info.getNodeUrl(),
            info.getFragments().size()
        );
    }

    @GetMapping("/nodes")
    public Map<String, Map<String, Object>> getAllNodes() {
        return registryService.getAllNodes();
    }

    @GetMapping("/fragment/{fragmentId}")
    public String findFragment(@PathVariable String fragmentId) {
        Optional<String> location = registryService.getFragmentLocation(fragmentId);
        return location.map(url -> "Fragment available at: " + url)
                      .orElse("Fragment not found in the network");
    }
}