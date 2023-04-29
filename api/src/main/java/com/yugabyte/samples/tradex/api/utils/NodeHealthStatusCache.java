package com.yugabyte.samples.tradex.api.utils;

import com.yugabyte.samples.tradex.api.web.dto.YBNode;
import com.yugabyte.samples.tradex.api.web.dto.YBRegion;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class NodeHealthStatusCache {
    Map<String, YBRegion> regionCache = new HashMap<>();
    Map<String, YBNode> ipNodeCache = new HashMap<>();

    public void addRegionEntry(String regionCode, YBRegion region) {
        regionCache.put(regionCode, region);
    }

    public void clearRegions() {
        regionCache.clear();
    }

    public YBRegion getRegion(String regionCode) {
        return regionCache.get(regionCode);
    }

    public Collection<YBRegion> getAllRegions() {
        return regionCache.values();
    }

    public void addYBNode(String ip, YBNode node) {
        ipNodeCache.put(ip, node);
    }

    public void clearNodes() {
        ipNodeCache.clear();
    }

    public YBNode getNode(String ip) {
        return ipNodeCache.get(ip);
    }

    public Collection<YBNode> getAllNodes() {
        return ipNodeCache.values();
    }

}

