package com.yugabyte.samples.tradex.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.samples.tradex.api.web.dto.YBNode;
import com.yugabyte.samples.tradex.api.web.dto.YBRegion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class NodeHealthMonitor {


    NodeHealthStatusCache cache;
    String ybAPIHost;

    String ybAPICustId;
    String ybAPIAuthToken;

    ObjectMapper mapper = new ObjectMapper();

    String baseURL;

    String nodeHealthURLTemplate = "%s/api/v1/customers/%s/zones/%zoneId/nodes/list";

    HttpHeaders defaultHeaders = new HttpHeaders();

    RestTemplate restTemplate;

    public NodeHealthMonitor(RestTemplate restTemplate,
                             NodeHealthStatusCache cache, @Value("${app.yb.api.host}") String ybAPIHost,
                             @Value("${app.yb.api.custid}") String ybAPICustId,
                             @Value("${app.yb.api.authtoken}") String ybAPIAuthToken) {
        this.cache = cache;
        this.ybAPIHost = ybAPIHost;
        this.ybAPICustId = ybAPICustId;
        this.ybAPIAuthToken = ybAPIAuthToken;
        this.restTemplate = restTemplate;
        this.baseURL = String.format("%s/api/v1/customers/%s", ybAPIHost, ybAPICustId);

        defaultHeaders.setContentType(MediaType.APPLICATION_JSON);
        defaultHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        defaultHeaders.setAcceptCharset(List.of(Charset.defaultCharset()));
        defaultHeaders.add("X-AUTH-YW-API-TOKEN", ybAPIAuthToken);
    }


    @EventListener(ContextRefreshedEvent.class)
    public void loadAtStart() {
        fetchRegionInfo();
        fetchZoneNodesInfo();
    }

    @Scheduled(cron = "${app.nodehealth.monitor.schedule}")
    public void refreshNodeInfo() {
        fetchZoneNodesInfo();
    }

    private void fetchRegionInfo() {
        log.debug("Fetching Regions Location info from API");
        try {
            HttpEntity<Void> request = new HttpEntity<>(defaultHeaders);
            ResponseEntity<String> actualRegionJson = restTemplate.exchange(baseURL
                    + "/regions", HttpMethod.GET, request, String.class);
            JsonNode jsonNode = mapper.readTree(actualRegionJson.getBody());

            int i = 1;
            if (jsonNode.isArray()) {
                for (JsonNode ele : jsonNode) {
                    i++;
                    YBRegion region = mapper.treeToValue(ele, YBRegion.class);
                    log.debug("Region: {}", region);
                    cache.addRegionEntry(region.getCode(), region);
                }
            }
            log.info("Regions fetch from api: {}", cache.getAllRegions().size());

        } catch (JsonProcessingException e) {
            log.error("Failed to fetch and parse node locations from API: {} . message: {}",
                    baseURL + "/regions", e.getMessage());
            //throw e;
        } catch (ResourceAccessException e) {
            log.error("Failed to fetch node locations from API: {}. message: {}",
                    baseURL + "/regions", e.getMessage());
        }
        log.debug("Fetched Node Location info from API");

    }

    private void fetchZoneNodesInfo() {
        log.trace("Fetching Nodes info from API");

        cache.getAllRegions().forEach(region -> region.getZones().forEach(ybZone -> {
            fetchNodesFromZone(ybZone.getUuid());
        }));
        log.trace("Completed Nodes fetch from API");
    }

    private void fetchNodesFromZone(UUID zoneId) {
        int count = 0;

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Void> request = new HttpEntity<>(defaultHeaders);

            ResponseEntity<String> actualRegionJson = restTemplate.exchange(baseURL
                    + "/zones/" + zoneId + "/nodes/list", HttpMethod.GET, request, String.class);
            JsonNode jsonNode = mapper.readTree(actualRegionJson.getBody());


            if (jsonNode.isArray()) {
                for (JsonNode ele : jsonNode) {
                    count++;
                    YBNode node = mapper.treeToValue(ele, YBNode.class);
                    log.debug("Node: {}", node);
                    cache.addYBNode(node.getIp(), node);
                }
            }

        } catch (JsonProcessingException e) {
            log.error("Failed to fetch and parse node of zone {} from API. message: {}", zoneId, e.getMessage());
            //throw e;
        } catch (ResourceAccessException e) {
            log.error("Failed to fetch and parse nodes of zone:{} from API: {}. message: {}", zoneId, baseURL, e.getMessage());
        }
        log.debug("Fetched {} Nodes of Zone {} info from API", count, zoneId);
    }

}
