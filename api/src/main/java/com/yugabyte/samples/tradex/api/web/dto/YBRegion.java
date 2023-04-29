package com.yugabyte.samples.tradex.api.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties
public class YBRegion {
    private boolean active;
    private UUID uuid;
    private String code;
    private String latitude;
    private String longitude;
    private String name;
    private List<YBZone> zones;

}
