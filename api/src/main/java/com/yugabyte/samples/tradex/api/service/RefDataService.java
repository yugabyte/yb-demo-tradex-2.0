package com.yugabyte.samples.tradex.api.service;

import static org.springframework.beans.BeanUtils.copyProperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.DBClusterInfo;
import com.yugabyte.samples.tradex.api.domain.business.DBNode;
import com.yugabyte.samples.tradex.api.domain.business.Location;
import com.yugabyte.samples.tradex.api.domain.business.StockSymbol;
import com.yugabyte.samples.tradex.api.domain.repo.RefdataRepo;
import com.yugabyte.samples.tradex.api.domain.repo.StockRepo;
import com.yugabyte.samples.tradex.api.utils.AppConstants;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@Transactional(readOnly = true)
public class RefDataService {

  @Value("${app.yb.api.host}")
  private String ybAPIHost;

  @Value("${app.yb.api.custid}")
  private String ybAPICustId;

  @Value("${app.yb.api.authtoken}")
  private String ybAPIAuthToken;

  private final StockRepo stockRepo;

  private final RefdataRepo refdataRepo;
  private final RestTemplate restTemplate;

  private final ObjectMapper applicationObjectMapper;

  public RefDataService(StockRepo stockRepo, RefdataRepo refdataRepo, RestTemplate restTemplate,
    @Qualifier("applicationObjectMapper") ObjectMapper applicationObjectMapper) {
    this.stockRepo = stockRepo;
    this.refdataRepo = refdataRepo;
    this.restTemplate = restTemplate;
    this.applicationObjectMapper = applicationObjectMapper;
  }

  @Cacheable("TRAFFIC_LOCATIONS")
  public List<Location> getTrafficLocations(TradeXDataSourceType dataSourceType)
    throws ApplicationServiceException {
    return refdataRepo.fetchRefDataAsList(dataSourceType,
      AppConstants.Caches.TRAFFIC_LOCATIONS.name(), Location.class);
  }

  @Cacheable("DB_CLUSTER_TYPES")
  public List<DBClusterInfo> getDbClusterTypes(TradeXDataSourceType dataSourceType)
    throws ApplicationServiceException {
    return refdataRepo.fetchRefDataAsList(dataSourceType,
      AppConstants.Caches.DB_CLUSTER_TYPES.name(), DBClusterInfo.class);
  }

  @Cacheable("DEFAULT_NODE_LOCATIONS")
  public List<Location> getDefaultNodeLocations(TradeXDataSourceType dataSourceType)
    throws ApplicationServiceException {
    return refdataRepo.fetchRefDataAsList(dataSourceType,
      AppConstants.Caches.DEFAULT_NODE_LOCATIONS.name(), Location.class);
  }

  @Cacheable("STOCK_SYMBOLS")
  public List<StockSymbol> getStockSymbols(TradeXDataSourceType dataSourceType) {
    return stockRepo.getAllActiveTradeXStocks(dataSourceType)
      .stream()
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
  public List<DBNode> getDBNodes(TradeXDataSourceType dataSourceType)
    throws ApplicationServiceException {

    Map<String, Location> nodeLocations = getNodeLocations();
    Map<String, Location> defaultNodeLocations = getDefaultNodeLocations(dataSourceType).stream()
      .collect(Collectors.toMap(Location::getName, Function.identity()));

    return refdataRepo.fetchDBNodes(dataSourceType)
      .stream()
      .map(e -> {

        String regionLookupKey = StringUtils.replace(e.getRegion(), "-", "-");
        Location loc =
          StringUtils.isEmpty(e.getRegion()) ? null : nodeLocations.get(regionLookupKey);

        if (defaultNodeLocations.containsKey(regionLookupKey)) {
          loc = defaultNodeLocations.get(regionLookupKey);
        } else {
          log.warn("Location details are missing for lookupKey: {}", regionLookupKey);
        }
        e.setLocation(loc);
        return e;
      })
      .collect(Collectors.toList());
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

      ResponseEntity<String> actualRegionJson = restTemplate.exchange(url, HttpMethod.GET, request2,
        String.class);
//      JsonNode jsonNode = applicationObjectMapper.readTree(actualRegionJson.getBody());
//
//      int i = 1;
//      if (jsonNode.isArray()) {
//        for (JsonNode ele : jsonNode) {
//          Location location = new Location();
//          location.setId(i);
//          location.setName(ele.get("code")
//            .textValue());
//          location.setLatitude(ele.get("latitude")
//            .doubleValue());
//          location.setLongitude(ele.get("longitude")
//            .doubleValue());
//          nodeLocations.put(location.getName()
//            .replace("-", ""), location);
//          i++;
//        }
//      }
      YBARegion[] regions = applicationObjectMapper.readValue(actualRegionJson.getBody(),
        YBARegion[].class);
      IntStream.range(0, regions.length)
        .mapToObj(i -> {
          var r = regions[i];
          var l = r.asLocation();
          l.setId(i);
          return l;
        })
        .forEach(l -> nodeLocations.put(l.getName()
          .replace("-", ""), l));

      log.info("Node Locations fetch from api: {}", nodeLocations);

    } catch (JsonProcessingException e) {
      log.error("Failed to fetch and parse node locations from API. url: {}, message: {}", url,
        e.getMessage());
      //throw e;
    } catch (ResourceAccessException e) {
      log.error("Failed to fetch node locations from API: {}. message: {}", url, e.getMessage());
    }
    log.debug("Fetched Node Location info from API");
    return nodeLocations;
  }

  record YBARegion(String code, Double latitude, Double longitude) {

    public Location asLocation() {
      Location location = new Location();
      location.setName(this.code());
      location.setLatitude(this.latitude());
      location.setLongitude(this.longitude());
      return location;
    }
  }

}
