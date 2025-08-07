package com.p2pStream.centralservice.dto;

import java.time.Instant;

public class FragmentEvent {
    private String fragmentId;
    private String nodeUrl;  // Changed from nodeId to nodeUrl for consistency
    private Long timestamp;

    // Constructors
    public FragmentEvent() {
        this.timestamp = Instant.now().toEpochMilli();
    }

    public FragmentEvent(String fragmentId, String nodeUrl) {
        this.fragmentId = fragmentId;
        this.nodeUrl = nodeUrl;
        this.timestamp = Instant.now().toEpochMilli();
    }

    // Getters and Setters
    public String getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(String fragmentId) {
        this.fragmentId = fragmentId;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    // Validation method
    public boolean isValid() {
        return fragmentId != null && !fragmentId.isEmpty() &&
               nodeUrl != null && !nodeUrl.isEmpty();
    }

    // toString for better logging
    @Override
    public String toString() {
        return "FragmentEvent{" +
                "fragmentId='" + fragmentId + '\'' +
                ", nodeUrl='" + nodeUrl + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
