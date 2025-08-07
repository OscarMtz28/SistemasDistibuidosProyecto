package com.p2pStream.centralservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.p2pStream.centralservice.dto.NodeInfo;
import com.p2pStream.centralservice.service.RegistryService;




@RestController
@RequestMapping("/api")
public class RegistryController {

    private final RegistryService registry;

    public RegistryController(RegistryService registry) {
        this.registry = registry;
    }

    @PostMapping("/register")
    public String registerNode(@RequestBody NodeInfo info) {
        registry.registerNode(info);
        return "Node registered";
    }

    @GetMapping("/nodes")
    public Map<String, List<String>> getAllNodes() {
        return registry.getAllNodes();
    }

    @GetMapping("/fragment/{fragmentId}")
    public String findFragment(@PathVariable String fragmentId) {
        return registry.getFragmentLocation(fragmentId)
                       .orElse("Fragment not found");
    }
}
