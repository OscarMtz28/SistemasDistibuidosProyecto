package com.p2pStream.centralservice.dto;

import java.util.List;

public class NodeInfo {
    private String nodeId;
    private List<String> fragments; // ej: ["part_0", "part_4", "part_7"]
    public String getNodeId() {
        return nodeId;
    }
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    public List<String> getFragments() {
        return fragments;
    }
    public void setFragments(List<String> fragments) {
        this.fragments = fragments;
    }    
}
