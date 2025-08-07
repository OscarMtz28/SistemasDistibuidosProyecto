package com.nodo.p2pnodo.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FragmentInfo {
    private String fragmentId;
    private String nodeUrl;  // Using URL instead of ID for direct node access
    private Long timestamp = Instant.now().toEpochMilli();  // Auto-initialized timestamp

  
    public boolean isValid() {
        return fragmentId != null && !fragmentId.isBlank()
                && nodeUrl != null && !nodeUrl.isBlank();
    }

   
    public static FragmentInfo of(String fragmentId, String nodeUrl) {
        return new FragmentInfo(fragmentId, nodeUrl, Instant.now().toEpochMilli());
    }

    
    @Override
    public String toString() {
        return String.format(
            "FragmentInfo{fragmentId='%s', nodeUrl='%s', timestamp=%d (ISO: %s)}",
            fragmentId,
            nodeUrl,
            timestamp,
            Instant.ofEpochMilli(timestamp).toString()
        );
    }

    
    public boolean isOlderThan(long maxAgeMs) {
        return (Instant.now().toEpochMilli() - timestamp) > maxAgeMs;
    }
}