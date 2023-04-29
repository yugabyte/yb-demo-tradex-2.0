package com.yugabyte.samples.tradex.api.web.controllers;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.DBClusterInfo;
import com.yugabyte.samples.tradex.api.domain.business.DBNode;
import com.yugabyte.samples.tradex.api.domain.business.Location;
import com.yugabyte.samples.tradex.api.domain.business.StockSymbol;
import com.yugabyte.samples.tradex.api.service.ApplicationServiceException;
import com.yugabyte.samples.tradex.api.service.RefDataService;
import com.yugabyte.samples.tradex.api.utils.NodeHealthStatusCache;
import com.yugabyte.samples.tradex.api.web.dto.YBNode;
import com.yugabyte.samples.tradex.api.web.dto.YBRegion;
import com.yugabyte.samples.tradex.api.web.utils.TradeXDBTypeContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
public class ReferenceDataController {

    final Map<String, String> proximityMap = Map.of("WASHINGTON", "USWEST2", "BOSTON",
            "USEAST1", "LONDON", "euwest2", "MUMBAI", "apsouth1", "SYDNEY", "apsoutheast2");

    //TODO move to refdata table
    final Map<String, String> multiRegionProximityMap = Map.of("WASHINGTON", "USWEST1", "BOSTON",
            "USEAST1", "LONDON", "useast2", "MUMBAI", "useast2", "SYDNEY", "useast2");
    final Map<String, String> readReplicaProximityMap = Map.of("WASHINGTON", "USWEST1", "BOSTON",
            "USEAST1", "LONDON", "USEAST1", "MUMBAI", "apsouth1", "SYDNEY", "apsouth1");
    final Map<String, String> geoProximityMap = Map.of("WASHINGTON", "USWEST2", "BOSTON",
            "USEAST1", "LONDON", "euwest2", "MUMBAI", "apsouth1", "SYDNEY", "apsoutheast2");
    @Autowired
    NodeHealthStatusCache nodeHealthStatusCache;
    @Autowired
    RefDataService refDataService;
    @Value("${app.mylocation}")
    String instanceLocation;

    @GetMapping("/api/refdata/trafficLoc")
    public List<Location> getTrafficLocations() throws ApplicationServiceException {
        return refDataService.getTrafficLocations(TradeXDBTypeContext.getDbType());
    }

    @GetMapping("/api/refdata/dbtypes")
    public List<DBClusterInfo> getDbTypes() throws ApplicationServiceException {
        return refDataService.getDbClusterTypes(TradeXDBTypeContext.getDbType());
    }

    @GetMapping("/api/refdata/symbols")
    public List<StockSymbol> getStockSymbols() {
        return refDataService.getStockSymbols(TradeXDBTypeContext.getDbType());
    }

    @GetMapping("/api/refdata/dbnodes")
    public List<DBNode> getDbNodes() throws ApplicationServiceException {
        return refDataService.getDBNodes(TradeXDBTypeContext.getDbType());
    }

    @GetMapping("/api/refdata/dbnodeLocations")
    public Collection<Location> getDbNodeLocations() {
        return refDataService.getNodeLocations().values();
    }

    @GetMapping("/api/refdata/optimalDBNode")
    public DBNode getOptimalNode(HttpServletRequest request) throws ApplicationServiceException {

        log.debug("AppServer: {}, User Location: {}, DB Cluster Type:{}", instanceLocation,
                request.getHeader("x-user-location"), request.getHeader("x-tradex-db-type"));

        if (TradeXDataSourceType.SINGLE_REGION_MULTI_ZONE.equals(TradeXDBTypeContext.getDbType())) {
            log.debug("Fetching first node from single");
            return getDbNodes().get(0);
        }

        return getCloseNode(TradeXDBTypeContext.getDbType(), request.getHeader("x-user-location"));
    }

    @GetMapping("/api/refdata/dbhealth/regions")
    public Collection<YBRegion> getAllRegions() {
        return nodeHealthStatusCache.getAllRegions();
    }

    @GetMapping("/api/refdata/dbhealth/nodes")
    public Collection<YBNode> getAllNodes() {
        return nodeHealthStatusCache.getAllNodes();
    }

    @GetMapping("/api/refdata/dbhealth/node/{ip}/status")
    public NodeStatus getNodeStatus(@PathParam("ip") String ipAddress) {
        YBNode node = nodeHealthStatusCache.getNode(ipAddress);
        return new NodeStatus(node.getNodeName(), node.getIp(), node.isInUse());
    }

    private DBNode getCloseNode(TradeXDataSourceType dbType, String userLocation) throws ApplicationServiceException {

        String originLoc;
        if (userLocation == null) {
            originLoc = StringUtils.upperCase(instanceLocation);
        } else {
            originLoc = StringUtils.upperCase(userLocation);
        }
        List<DBNode> dbNodes = getDbNodes();
        dbNodes.forEach(e -> {
            log.debug("id: {} region: {} zone: {}", e.getId(), e.getRegion(), e.getZone());
        });

        Map<String, String> closestNodeMap = switch (dbType) {
            case MULTI_REGION_MULTI_ZONE -> multiRegionProximityMap;
            case MULTI_REGION_READ_REPLICA -> readReplicaProximityMap;
            case GEO_PARTITIONED -> geoProximityMap;
            default -> proximityMap;
        };

        log.debug("Proximity Map: {}", closestNodeMap);
        String p1 = closestNodeMap.get(StringUtils.upperCase(originLoc));
        log.debug("Found match for instanceLocation: {}, userLocation: {} -> DB Node  :{}",
                instanceLocation, userLocation, p1);

        if (StringUtils.isEmpty(p1)) {
            log.warn("p1: {} for {}", p1, originLoc);
            return dbNodes.get(0);
        }

        return dbNodes.stream()
                .filter(e -> p1.equalsIgnoreCase(StringUtils.replace(e.getRegion(), "-", "")))
                .findFirst().orElse(dbNodes.get(0));

    }

}

record NodeStatus(String nodeName, String ipAddress, boolean inUse) {
}
