package com.yugabyte.samples.tradex.api.web.dto;

public record ConnectionInfo(String host, String cloud, String region, String zone, String partitionKey) {
}
