package com.p2pStream.centralservice.dto;

import java.util.List;

public class NodeRegistration {
    private String nodeId;
    private String nodeUrl;
    private List<String> fragments;

    

    // Getters y Setters
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public List<String> getFragments() {
        return fragments;
    }

    public void setFragments(List<String> fragments) {
        this.fragments = fragments;
    }

    
}