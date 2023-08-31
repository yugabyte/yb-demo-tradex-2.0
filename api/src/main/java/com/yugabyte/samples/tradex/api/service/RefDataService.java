package com.yugabyte.samples.tradex.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.DBClusterInfo;
import com.yugabyte.samples.tradex.api.domain.business.DBNode;
import com.yugabyte.samples.tradex.api.domain.business.Location;
import com.yugabyte.samples.tradex.api.domain.business.StockSymbol;
import com.yugabyte.samples.tradex.api.domain.repo.RefdataRepo;
import com.yugabyte.samples.tradex.api.domain.repo.StockRepo;
import com.yugabyte.samples.tradex.api.utils.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Slf4j
@Transactional(readOnly = true)
public class RefDataService {

    final ObjectMapper mapper = new ObjectMapper();

    @Value("${app.yb.api.host}")
    String ybAPIHost;

    @Value("${app.yb.api.custid}")
    String ybAPICustId;

    @Value("${app.yb.api.authtoken}")
    String ybAPIAuthToken;

    @Autowired
    StockRepo stockRepo;

    @Autowired
    RefdataRepo refdataRepo;
    @Autowired
    RestTemplate restTemplate;

    @Cacheable("TRAFFIC_LOCATIONS")
    public List<Location> getTrafficLocations(TradeXDataSourceType dataSourceType) throws ApplicationServiceException {
        return refdataRepo.fetchRefDataAsList(dataSourceType, AppConstants.Caches.TRAFFIC_LOCATIONS.name(), Location.class);
    }

    @Cacheable("DB_CLUSTER_TYPES")
    public List<DBClusterInfo> getDbClusterTypes(TradeXDataSourceType dataSourceType) throws ApplicationServiceException {
        return refdataRepo.fetchRefDataAsList(dataSourceType, AppConstants.Caches.DB_CLUSTER_TYPES.name(), DBClusterInfo.class);
    }

    @Cacheable("DEFAULT_NODE_LOCATIONS")
    public List<Location> getDefaultNodeLocations(TradeXDataSourceType dataSourceType) throws ApplicationServiceException {
        return refdataRepo.fetchRefDataAsList(dataSourceType, AppConstants.Caches.DEFAULT_NODE_LOCATIONS.name(), Location.class);
    }

    @Cacheable("STOCK_SYMBOLS")
    public List<StockSymbol> getStockSymbols(TradeXDataSourceType dataSourceType) {
        return stockRepo.getAllActiveTradeXStocks(dataSourceType).stream()
                .map(e -> {
                    StockSymbol s = new StockSymbol();
                    copyProperties(e, s, StockSymbol.class);
                    return s;
                })
                .collect(Collectors.toList());
    }


    @Cacheable("DB_NODES_LOCATIONS")
    public Map<String, Location> getNodeLocations() {
        return fetchDBNodeLocationsFromApi();
    }

    @Cacheable("DB_NODES")
    public List<DBNode> getDBNodes(TradeXDataSourceType dataSourceType) throws ApplicationServiceException {

        Map<String, Location> nodeLocations = getNodeLocations();
        Map<String, Location> defaultNodeLocations = getDefaultNodeLocations(dataSourceType).stream()
                .collect(Collectors.toMap(Location::getName, Function.identity()));


        return refdataRepo.fetchDBNodes(dataSourceType).stream().map(e -> {

            String regionLookupKey = StringUtils.replace(e.getRegion(), "-", "-");
            Location loc = StringUtils.isEmpty(e.getRegion()) ? null : nodeLocations.get(regionLookupKey);

            if (defaultNodeLocations.containsKey(regionLookupKey)) {
                loc = defaultNodeLocations.get(regionLookupKey);
            } else {
                log.warn("Location details are missing for lookupKey: {}", regionLookupKey);
            }
            e.setLocation(loc);
            return e;
        }).collect(Collectors.toList());
    }


    @Cacheable("API_NODE_LOCATIONS")
    public Map<String, Location> fetchDBNodeLocationsFromApi() {
        log.debug("Fetching Node Location info from API");
        String url = String.format("%s/api/v1/customers/%s/regions", ybAPIHost, ybAPICustId);
        Map<String, Location> nodeLocations = new HashMap<>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setAcceptCharset(List.of(Charset.defaultCharset()));

            headers.add("X-AUTH-YW-API-TOKEN", ybAPIAuthToken);
            HttpEntity<Void> request2 = new HttpEntity<>(headers);

            ResponseEntity<String> actualRegionJson = restTemplate.exchange(url, HttpMethod.GET, request2, String.class);
            JsonNode jsonNode = mapper.readTree(actualRegionJson.getBody());


            int i = 1;
            if (jsonNode.isArray()) {
                for (JsonNode ele : jsonNode) {
                    Location location = new Location();
                    location.setId(i);
                    location.setName(ele.get("code").textValue());
                    location.setLatitude(ele.get("latitude").doubleValue());
                    location.setLongitude(ele.get("longitude").doubleValue());
                    nodeLocations.put(location.getName().replace("-", ""), location);
                    i++;
                }
            }
            log.info("Node Locations fetch from api: {}", nodeLocations);

        } catch (JsonProcessingException e) {
            log.error("Failed to fetch and parse node locations from API. message: {}", url, e.getMessage());
            //throw e;
        } catch (ResourceAccessException e) {
            log.error("Failed to fetch node locations from API: {}. message: {}", url, e.getMessage());
        }
        log.debug("Fetched Node Location info from API");
        return nodeLocations;
    }

}
